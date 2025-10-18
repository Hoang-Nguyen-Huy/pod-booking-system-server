-- =============================================
-- FlexiPod Database Creation Script
-- Generated from JPA Entities
-- =============================================

-- Create database
CREATE DATABASE IF NOT EXISTS `podDatabase` 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `podDatabase`;

-- =============================================
-- Table: building
-- =============================================
CREATE TABLE `building` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `status` ENUM('Active', 'Inactive') NOT NULL DEFAULT 'Active',
    `address` VARCHAR(255) NOT NULL,
    `description` TEXT,
    `hotlineNumber` VARCHAR(20),
    `createdAt` DATE NOT NULL,
    `updatedAt` DATE NOT NULL
);

-- =============================================
-- Table: account
-- =============================================
CREATE TABLE `account` (
    `id` VARCHAR(36) PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `avatar` VARCHAR(500),
    `point` INT DEFAULT 0,
    `phoneNumber` VARCHAR(20),
    `role` ENUM('Customer', 'Staff', 'Admin', 'Manager') NOT NULL DEFAULT 'Customer',
    `balance` DOUBLE DEFAULT 0.0,
    `buildingNumber` INT DEFAULT 0,
    `createdAt` DATE NOT NULL,
    `status` INT NOT NULL DEFAULT 1,
    INDEX `idx_email` (`email`),
    INDEX `idx_building` (`buildingNumber`)
);

-- =============================================
-- Table: refreshToken
-- =============================================
CREATE TABLE `refreshToken` (
    `id` VARCHAR(36) PRIMARY KEY,
    `token` VARCHAR(500) NOT NULL,
    `accountId` VARCHAR(36) NOT NULL,
    `createdAt` DATETIME NOT NULL,
    `expiredAt` DATETIME NOT NULL,
    FOREIGN KEY (`accountId`) REFERENCES `account`(`id`) ON DELETE CASCADE,
    INDEX `idx_account` (`accountId`),
    INDEX `idx_token` (`token`(255))
);

-- =============================================
-- Table: servicePackage
-- =============================================
CREATE TABLE `servicePackage` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `description` TEXT,
    `price` DOUBLE NOT NULL DEFAULT 0.0,
    `discountPercentage` DOUBLE DEFAULT 0.0,
    `createdAt` DATETIME NOT NULL,
    `updatedAt` DATETIME NOT NULL
);

-- =============================================
-- Table: roomtype
-- =============================================
CREATE TABLE `roomtype` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `price` INT NOT NULL,
    `quantity` INT NOT NULL,
    `capacity` INT NOT NULL,
    `buildingId` INT,
    `image` VARCHAR(1000),
    `createdAt` DATE NOT NULL,
    `updatedAt` DATE NOT NULL,
    FOREIGN KEY (`buildingId`) REFERENCES `building`(`id`) ON DELETE SET NULL,
    INDEX `idx_building` (`buildingId`)
);

-- =============================================
-- Table: room
-- =============================================
CREATE TABLE `room` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `description` TEXT,
    `image` VARCHAR(1000),
    `status` ENUM('Available', 'Occupied', 'Maintenance', 'Unavailable') NOT NULL DEFAULT 'Available',
    `createdAt` DATE NOT NULL,
    `updatedAt` DATE NOT NULL,
    `typeId` INT,
    FOREIGN KEY (`typeId`) REFERENCES `roomtype`(`id`) ON DELETE SET NULL,
    INDEX `idx_type` (`typeId`),
    INDEX `idx_status` (`status`)
);

-- =============================================
-- Table: amenity
-- =============================================
CREATE TABLE `amenity` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `price` DOUBLE NOT NULL,
    `quantity` INT NOT NULL,
    `type` ENUM('Office', 'Food') NOT NULL,
    `imageUrl` VARCHAR(500),
    `createdAt` DATETIME NOT NULL,
    `updatedAt` DATETIME NOT NULL,
    `isDeleted` INT DEFAULT 0,
    `building_id` INT,
    FOREIGN KEY (`building_id`) REFERENCES `building`(`id`) ON DELETE SET NULL,
    INDEX `idx_building` (`building_id`),
    INDEX `idx_type` (`type`),
    INDEX `idx_deleted` (`isDeleted`)
);

-- =============================================
-- Table: order
-- =============================================
CREATE TABLE `order` (
    `id` VARCHAR(36) PRIMARY KEY,
    `accountId` VARCHAR(36) NOT NULL,
    `createdAt` DATETIME NOT NULL,
    `updatedAt` DATETIME NOT NULL,
    FOREIGN KEY (`accountId`) REFERENCES `account`(`id`) ON DELETE CASCADE,
    INDEX `idx_account` (`accountId`),
    INDEX `idx_created` (`createdAt`)
);

-- =============================================
-- Table: orderDetail
-- =============================================
CREATE TABLE `orderDetail` (
    `id` VARCHAR(36) PRIMARY KEY,
    `customerId` VARCHAR(36),
    `buildingNumber` INT,
    `roomId` INT NOT NULL,
    `orderId` VARCHAR(36) NOT NULL,
    `servicePackageId` INT,
    `orderHandlerId` VARCHAR(36),
    `priceRoom` DOUBLE NOT NULL,
    `discountPercentage` DOUBLE DEFAULT 0.0,
    `startTime` DATETIME NOT NULL,
    `endTime` DATETIME NOT NULL,
    `status` ENUM('Pending', 'Confirmed', 'Cancelled', 'Completed') NOT NULL DEFAULT 'Pending',
    `createdAt` DATETIME NOT NULL,
    `updatedAt` DATETIME NOT NULL,
    FOREIGN KEY (`customerId`) REFERENCES `account`(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`buildingNumber`) REFERENCES `building`(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`roomId`) REFERENCES `room`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`orderId`) REFERENCES `order`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`servicePackageId`) REFERENCES `servicePackage`(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`orderHandlerId`) REFERENCES `account`(`id`) ON DELETE SET NULL,
    INDEX `idx_customer` (`customerId`),
    INDEX `idx_room` (`roomId`),
    INDEX `idx_order` (`orderId`),
    INDEX `idx_time` (`startTime`, `endTime`),
    INDEX `idx_status` (`status`)
);

-- =============================================
-- Table: orderDetailAmenity
-- =============================================
CREATE TABLE `orderDetailAmenity` (
    `id` VARCHAR(36) PRIMARY KEY,
    `orderDetailId` VARCHAR(36) NOT NULL,
    `amenityId` INT NOT NULL,
    `quantity` INT NOT NULL,
    `price` DOUBLE NOT NULL,
    `createdAt` DATETIME NOT NULL,
    `updatedAt` DATETIME NOT NULL,
    FOREIGN KEY (`orderDetailId`) REFERENCES `orderDetail`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`amenityId`) REFERENCES `amenity`(`id`) ON DELETE CASCADE,
    INDEX `idx_order_detail` (`orderDetailId`),
    INDEX `idx_amenity` (`amenityId`)
);

-- =============================================
-- Table: assignment
-- =============================================
CREATE TABLE `assignment` (
    `id` VARCHAR(36) PRIMARY KEY,
    `staffId` VARCHAR(36) NOT NULL,
    `slot` VARCHAR(50) NOT NULL,
    `weekDate` VARCHAR(20) NOT NULL,
    INDEX `idx_staff` (`staffId`),
    INDEX `idx_week_slot` (`weekDate`, `slot`)
);

-- =============================================
-- Table: roomImage (if exists)
-- =============================================
CREATE TABLE `roomImage` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `roomId` INT NOT NULL,
    `imageUrl` VARCHAR(500) NOT NULL,
    `createdAt` DATETIME NOT NULL,
    `updatedAt` DATETIME NOT NULL,
    FOREIGN KEY (`roomId`) REFERENCES `room`(`id`) ON DELETE CASCADE,
    INDEX `idx_room` (`roomId`)
);

-- =============================================
-- Insert Sample Data
-- =============================================

-- Sample Buildings
INSERT INTO `building` (`address`, `description`, `hotlineNumber`, `createdAt`, `updatedAt`) VALUES
('123 Nguyen Hue, District 1, Ho Chi Minh City', 'Main building in city center', '0901234567', CURDATE(), CURDATE()),
('456 Le Lai, District 3, Ho Chi Minh City', 'Branch building near university', '0907654321', CURDATE(), CURDATE());

-- Sample Admin Account
INSERT INTO `account` (`id`, `name`, `email`, `password`, `role`, `buildingNumber`, `createdAt`) VALUES
(UUID(), 'System Admin', 'admin@flexipod.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Admin', 1, CURDATE());

-- Sample Service Packages
INSERT INTO `servicePackage` (`name`, `description`, `price`, `discountPercentage`, `createdAt`, `updatedAt`) VALUES
('Basic Package', 'Basic room booking package', 100000, 0, NOW(), NOW()),
('Premium Package', 'Premium package with amenities', 200000, 10, NOW(), NOW()),
('VIP Package', 'VIP package with full service', 300000, 15, NOW(), NOW());

-- Sample Room Types
INSERT INTO `roomtype` (`name`, `price`, `quantity`, `capacity`, `buildingId`, `createdAt`, `updatedAt`) VALUES
('Single Pod', 50000, 10, 1, 1, CURDATE(), CURDATE()),
('Double Pod', 80000, 8, 2, 1, CURDATE(), CURDATE()),
('Group Pod', 120000, 5, 4, 1, CURDATE(), CURDATE()),
('Meeting Room', 200000, 3, 8, 1, CURDATE(), CURDATE());

-- Sample Amenities
INSERT INTO `amenity` (`name`, `price`, `quantity`, `type`, `building_id`, `createdAt`, `updatedAt`) VALUES
('Coffee', 25000, 100, 'Food', 1, NOW(), NOW()),
('Sandwich', 35000, 50, 'Food', 1, NOW(), NOW()),
('Printer Access', 10000, 5, 'Office', 1, NOW(), NOW()),
('Whiteboard', 15000, 10, 'Office', 1, NOW(), NOW()),
('Projector', 50000, 3, 'Office', 1, NOW(), NOW());

-- =============================================
-- Create Indexes for Performance
-- =============================================

-- Additional indexes for common queries
CREATE INDEX `idx_account_role_status` ON `account`(`role`, `status`);
CREATE INDEX `idx_orderDetail_time_status` ON `orderDetail`(`startTime`, `endTime`, `status`);
CREATE INDEX `idx_amenity_type_deleted` ON `amenity`(`type`, `isDeleted`);
CREATE INDEX `idx_room_status_type` ON `room`(`status`, `typeId`);

-- =============================================
-- Create Views for Common Queries
-- =============================================

-- View: Available rooms with type information
CREATE VIEW `available_rooms_view` AS
SELECT 
    r.id,
    r.name as room_name,
    rt.name as room_type,
    rt.price,
    rt.capacity,
    b.address as building_address,
    r.status
FROM room r
JOIN roomtype rt ON r.typeId = rt.id
JOIN building b ON rt.buildingId = b.id
WHERE r.status = 'Available' AND b.status = 'Active';

-- View: Order summary with customer info
CREATE VIEW `order_summary_view` AS
SELECT 
    o.id as order_id,
    a.name as customer_name,
    a.email as customer_email,
    COUNT(od.id) as total_items,
    SUM(od.priceRoom) as total_amount,
    o.createdAt
FROM `order` o
JOIN account a ON o.accountId = a.id
LEFT JOIN orderDetail od ON o.id = od.orderId
GROUP BY o.id;

-- =============================================
-- Create Triggers for Auto-Update
-- =============================================

-- Trigger: Update building updatedAt on change
DELIMITER $$
CREATE TRIGGER `building_update_timestamp`
    BEFORE UPDATE ON `building`
    FOR EACH ROW
BEGIN
    SET NEW.updatedAt = CURDATE();
END$$

-- Trigger: Update account point on order completion
CREATE TRIGGER `update_account_points`
    AFTER UPDATE ON `orderDetail`
    FOR EACH ROW
BEGIN
    IF NEW.status = 'Completed' AND OLD.status != 'Completed' THEN
        UPDATE account 
        SET point = point + FLOOR(NEW.priceRoom / 1000)
        WHERE id = NEW.customerId;
    END IF;
END$$

DELIMITER ;

-- =============================================
-- Final Setup Messages
-- =============================================
SELECT 'Database schema created successfully!' as Status;
SELECT 'Sample data inserted!' as Status;
SELECT 'Views and triggers created!' as Status;