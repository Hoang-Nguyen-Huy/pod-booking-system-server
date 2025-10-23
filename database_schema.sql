-- =============================================
-- FlexiPod Database Creation Script
-- Generated from JPA Entities
-- =============================================

-- Create database
CREATE
DATABASE IF NOT EXISTS `podDatabase`
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `podDatabase`;

-- =============================================
-- Table: building
-- =============================================
CREATE TABLE `building`
(
    `id`            INT AUTO_INCREMENT PRIMARY KEY,
    `status`        ENUM('Active', 'Inactive') NOT NULL DEFAULT 'Active',
    `address`       VARCHAR(255) NOT NULL,
    `description`   TEXT,
    `hotlineNumber` VARCHAR(20),
    `createdAt`     DATE         NOT NULL,
    `updatedAt`     DATE         NOT NULL
);

-- =============================================
-- Table: account
-- =============================================
CREATE TABLE `account`
(
    `id`             VARCHAR(36) PRIMARY KEY,
    `name`           VARCHAR(255) NOT NULL,
    `email`          VARCHAR(255) NOT NULL UNIQUE,
    `password`       VARCHAR(255) NOT NULL,
    `avatar`         VARCHAR(500),
    `point`          INT                   DEFAULT 0,
    `phoneNumber`    VARCHAR(20),
    `role`           ENUM('Customer', 'Staff', 'Admin', 'Manager') NOT NULL DEFAULT 'Customer',
    `balance` DOUBLE DEFAULT 0.0,
    `buildingNumber` INT                   DEFAULT 0,
    `createdAt`      DATE         NOT NULL,
    `status`         INT          NOT NULL DEFAULT 1,
    INDEX            `idx_email` (`email`),
    INDEX            `idx_building` (`buildingNumber`)
);

-- =============================================
-- Table: refreshToken
-- =============================================
CREATE TABLE `refreshToken`
(
    `id`        VARCHAR(36) PRIMARY KEY,
    `token`     VARCHAR(500) NOT NULL,
    `accountId` VARCHAR(36)  NOT NULL,
    `createdAt` DATETIME     NOT NULL,
    `expiredAt` DATETIME     NOT NULL,
    FOREIGN KEY (`accountId`) REFERENCES `account` (`id`) ON DELETE CASCADE,
    INDEX       `idx_account` (`accountId`),
    INDEX       `idx_token` (`token`(255))
);

-- =============================================
-- Table: servicePackage
-- =============================================
CREATE TABLE `servicePackage`
(
    `id`          INT AUTO_INCREMENT PRIMARY KEY,
    `name`        VARCHAR(255) NOT NULL,
    `description` TEXT,
    `price` DOUBLE NOT NULL DEFAULT 0.0,
    `discountPercentage` DOUBLE DEFAULT 0.0,
    `createdAt`   DATETIME     NOT NULL,
    `updatedAt`   DATETIME     NOT NULL
);

-- =============================================
-- Table: roomType
-- =============================================
CREATE TABLE `roomType`
(
    `id`         INT AUTO_INCREMENT PRIMARY KEY,
    `name`       VARCHAR(255) NOT NULL,
    `price`      INT          NOT NULL,
    `quantity`   INT          NOT NULL,
    `capacity`   INT          NOT NULL,
    `buildingId` INT,
    `image`      VARCHAR(1000),
    `createdAt`  DATE         NOT NULL,
    `updatedAt`  DATE         NOT NULL,
    FOREIGN KEY (`buildingId`) REFERENCES `building` (`id`) ON DELETE SET NULL,
    INDEX        `idx_building` (`buildingId`)
);

-- =============================================
-- Table: room
-- =============================================
CREATE TABLE `room`
(
    `id`          INT AUTO_INCREMENT PRIMARY KEY,
    `name`        VARCHAR(255) NOT NULL,
    `description` TEXT,
    `image`       VARCHAR(1000),
    `status`      ENUM('Available', 'Occupied', 'Maintenance', 'Unavailable') NOT NULL DEFAULT 'Available',
    `createdAt`   DATE         NOT NULL,
    `updatedAt`   DATE         NOT NULL,
    `typeId`      INT,
    FOREIGN KEY (`typeId`) REFERENCES `roomType` (`id`) ON DELETE SET NULL,
    INDEX         `idx_type` (`typeId`),
    INDEX         `idx_status` (`status`)
);

-- =============================================
-- Table: amenity
-- =============================================
CREATE TABLE `amenity`
(
    `id`          INT AUTO_INCREMENT PRIMARY KEY,
    `name`        VARCHAR(255) NOT NULL,
    `price` DOUBLE NOT NULL,
    `quantity`    INT          NOT NULL,
    `type`        ENUM('Office', 'Food') NOT NULL,
    `imageUrl`    VARCHAR(500),
    `createdAt`   DATETIME     NOT NULL,
    `updatedAt`   DATETIME     NOT NULL,
    `isDeleted`   INT DEFAULT 0,
    `building_id` INT,
    FOREIGN KEY (`building_id`) REFERENCES `building` (`id`) ON DELETE SET NULL,
    INDEX         `idx_building` (`building_id`),
    INDEX         `idx_type` (`type`),
    INDEX         `idx_deleted` (`isDeleted`)
);

-- =============================================
-- Table: order
-- =============================================
CREATE TABLE `order`
(
    `id`        VARCHAR(36) PRIMARY KEY,
    `accountId` VARCHAR(36) NOT NULL,
    `createdAt` DATETIME    NOT NULL,
    `updatedAt` DATETIME    NOT NULL,
    FOREIGN KEY (`accountId`) REFERENCES `account` (`id`) ON DELETE CASCADE,
    INDEX       `idx_account` (`accountId`),
    INDEX       `idx_created` (`createdAt`)
);

-- =============================================
-- Table: orderDetail
-- =============================================
CREATE TABLE `orderDetail`
(
    `id`               VARCHAR(36) PRIMARY KEY,
    `customerId`       VARCHAR(36),
    `buildingNumber`   INT,
    `roomId`           INT         NOT NULL,
    `orderId`          VARCHAR(36) NOT NULL,
    `servicePackageId` INT,
    `orderHandlerId`   VARCHAR(36),
    `priceRoom` DOUBLE NOT NULL,
    `discountPercentage` DOUBLE DEFAULT 0.0,
    `startTime`        DATETIME    NOT NULL,
    `endTime`          DATETIME    NOT NULL,
    `status`           ENUM('Pending', 'Confirmed', 'Cancelled', 'Completed') NOT NULL DEFAULT 'Pending',
    `createdAt`        DATETIME    NOT NULL,
    `updatedAt`        DATETIME    NOT NULL,
    FOREIGN KEY (`customerId`) REFERENCES `account` (`id`) ON DELETE SET NULL,
    FOREIGN KEY (`buildingNumber`) REFERENCES `building` (`id`) ON DELETE SET NULL,
    FOREIGN KEY (`roomId`) REFERENCES `room` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`orderId`) REFERENCES `order` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`servicePackageId`) REFERENCES `servicePackage` (`id`) ON DELETE SET NULL,
    FOREIGN KEY (`orderHandlerId`) REFERENCES `account` (`id`) ON DELETE SET NULL,
    INDEX              `idx_customer` (`customerId`),
    INDEX              `idx_room` (`roomId`),
    INDEX              `idx_order` (`orderId`),
    INDEX              `idx_time` (`startTime`, `endTime`),
    INDEX              `idx_status` (`status`)
);

-- =============================================
-- Table: orderDetailAmenity
-- =============================================
CREATE TABLE `orderDetailAmenity`
(
    `id`            VARCHAR(36) PRIMARY KEY,
    `orderDetailId` VARCHAR(36) NOT NULL,
    `amenityId`     INT         NOT NULL,
    `quantity`      INT         NOT NULL,
    `price` DOUBLE NOT NULL,
    `createdAt`     DATETIME    NOT NULL,
    `updatedAt`     DATETIME    NOT NULL,
    FOREIGN KEY (`orderDetailId`) REFERENCES `orderDetail` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`amenityId`) REFERENCES `amenity` (`id`) ON DELETE CASCADE,
    INDEX           `idx_order_detail` (`orderDetailId`),
    INDEX           `idx_amenity` (`amenityId`)
);

-- =============================================
-- Table: assignment
-- =============================================
CREATE TABLE `assignment`
(
    `id`       VARCHAR(36) PRIMARY KEY,
    `staffId`  VARCHAR(36) NOT NULL,
    `slot`     VARCHAR(50) NOT NULL,
    `weekDate` VARCHAR(20) NOT NULL,
    INDEX      `idx_staff` (`staffId`),
    INDEX      `idx_week_slot` (`weekDate`, `slot`)
);

-- =============================================
-- Table: roomImage
-- =============================================
CREATE TABLE `roomImage`
(
    `id`        INT AUTO_INCREMENT PRIMARY KEY,
    `roomId`    INT          NOT NULL,
    `imageUrl`  VARCHAR(500) NOT NULL,
    `createdAt` DATETIME     NOT NULL,
    `updatedAt` DATETIME     NOT NULL,
    FOREIGN KEY (`roomId`) REFERENCES `room` (`id`) ON DELETE CASCADE,
    INDEX       `idx_room` (`roomId`)
);

-- =============================================
-- Insert Sample Data
-- =============================================

-- Sample Buildings
INSERT INTO `building` (`address`, `description`, `hotlineNumber`, `createdAt`, `updatedAt`)
VALUES ('123 Nguyen Hue, District 1, Ho Chi Minh City', 'Main building in city center', '0901234567', CURDATE(),
        CURDATE()),
       ('456 Le Lai, District 3, Ho Chi Minh City', 'Branch building near university', '0907654321', CURDATE(),
        CURDATE());

-- Sample Admin Account
INSERT INTO `account` (`id`, `name`, `email`, `password`, `role`, `buildingNumber`, `createdAt`)
VALUES (UUID(), 'System Admin', 'admin@flexipod.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
        'Admin', 1, CURDATE());

-- Sample Service Packages
INSERT INTO `servicePackage` (`name`, `description`, `price`, `discountPercentage`, `createdAt`, `updatedAt`)
VALUES ('Basic Package', 'Basic room booking package', 100000, 0, NOW(), NOW()),
       ('Premium Package', 'Premium package with amenities', 200000, 10, NOW(), NOW()),
       ('VIP Package', 'VIP package with full service', 300000, 15, NOW(), NOW());

-- Sample Room Types
INSERT INTO `roomType` (`name`, `price`, `quantity`, `capacity`, `buildingId`, `createdAt`, `updatedAt`)
VALUES ('Single Pod', 50000, 10, 1, 1, CURDATE(), CURDATE()),
       ('Double Pod', 80000, 8, 2, 1, CURDATE(), CURDATE()),
       ('Group Pod', 120000, 5, 4, 1, CURDATE(), CURDATE()),
       ('Meeting Room', 200000, 3, 8, 1, CURDATE(), CURDATE());

-- Sample Amenities
INSERT INTO `amenity` (`name`, `price`, `quantity`, `type`, `building_id`, `createdAt`, `updatedAt`)
VALUES ('Coffee', 25000, 100, 'Food', 1, NOW(), NOW()),
       ('Sandwich', 35000, 50, 'Food', 1, NOW(), NOW()),
       ('Printer Access', 10000, 5, 'Office', 1, NOW(), NOW()),
       ('Whiteboard', 15000, 10, 'Office', 1, NOW(), NOW()),
       ('Projector', 50000, 3, 'Office', 1, NOW(), NOW());

-- =============================================
-- Indexes for Performance
-- =============================================
CREATE INDEX `idx_account_role_status` ON `account` (`role`, `status`);
CREATE INDEX `idx_orderDetail_time_status` ON `orderDetail` (`startTime`, `endTime`, `status`);
CREATE INDEX `idx_amenity_type_deleted` ON `amenity` (`type`, `isDeleted`);
CREATE INDEX `idx_room_status_type` ON `room` (`status`, `typeId`);

-- =============================================
-- Views
-- =============================================

CREATE VIEW `available_rooms_view` AS
SELECT r.id,
       r.name    AS room_name,
       rt.name   AS room_type,
       rt.price,
       rt.capacity,
       b.address AS building_address,
       r.status
FROM room r
         JOIN roomType rt ON r.typeId = rt.id
         JOIN building b ON rt.buildingId = b.id
WHERE r.status = 'Available'
  AND b.status = 'Active';

CREATE VIEW `order_summary_view` AS
SELECT o.id              AS order_id,
       a.name            AS customer_name,
       a.email           AS customer_email,
       COUNT(od.id)      AS total_items,
       SUM(od.priceRoom) AS total_amount,
       o.createdAt
FROM `order` o
         JOIN account a ON o.accountId = a.id
         LEFT JOIN orderDetail od ON o.id = od.orderId
GROUP BY o.id;

-- =============================================
-- Triggers
-- =============================================
DELIMITER $$
CREATE TRIGGER `building_update_timestamp`
    BEFORE UPDATE
    ON `building`
    FOR EACH ROW
BEGIN
    SET NEW.updatedAt = CURDATE();
END$$

    CREATE TRIGGER `update_account_points`
        AFTER UPDATE
        ON `orderDetail`
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
USE `podDatabase`;

-- =============================================
-- Fake Data: building
-- =============================================
    INSERT INTO `building` (`address`, `description`, `hotlineNumber`, `createdAt`, `updatedAt`)
    VALUES ('12 Pham Ngu Lao, District 1, HCMC', 'Co-working building near Ben Thanh Market', '0909111222', CURDATE(),
            CURDATE()),
           ('88 Vo Van Tan, District 3, HCMC', 'New branch with meeting rooms', '0909777333', CURDATE(), CURDATE()),
           ('45 Tran Hung Dao, District 5, HCMC', 'Affordable pods for students', '0909333444', CURDATE(), CURDATE());

    -- =============================================
-- Fake Data: account
-- =============================================
    INSERT INTO `account` (`id`, `name`, `email`, `password`, `role`, `balance`, `buildingNumber`, `createdAt`,
                           `status`)
    VALUES (UUID(), 'Nguyen Van A', 'a@flexipod.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
            'Customer', 200000, 1, CURDATE(), 1),
           (UUID(), 'Tran Thi B', 'b@flexipod.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
            'Staff', 500000, 2, CURDATE(), 1),
           (UUID(), 'Le Van C', 'c@flexipod.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
            'Manager', 1000000, 1, CURDATE(), 1);

    -- =============================================
-- Fake Data: servicePackage
-- =============================================
    INSERT INTO `servicePackage` (`name`, `description`, `price`, `discountPercentage`, `createdAt`, `updatedAt`)
    VALUES ('Starter Package', 'Includes 1 hour pod use', 50000, 0, NOW(), NOW()),
           ('Business Package', 'Includes 4-hour pod use with amenities', 150000, 10, NOW(), NOW()),
           ('Premium Office', '8-hour use, free coffee, and printer access', 300000, 15, NOW(), NOW());

    -- =============================================
-- Fake Data: roomType
-- =============================================
    INSERT INTO `roomType` (`name`, `price`, `quantity`, `capacity`, `buildingId`, `createdAt`, `updatedAt`)
    VALUES ('Single Pod', 60000, 10, 1, 1, CURDATE(), CURDATE()),
           ('Double Pod', 90000, 6, 2, 1, CURDATE(), CURDATE()),
           ('Meeting Room', 250000, 2, 8, 2, CURDATE(), CURDATE());

    -- =============================================
-- Fake Data: room
-- =============================================
    INSERT INTO `room` (`name`, `description`, `image`, `status`, `createdAt`, `updatedAt`, `typeId`)
    VALUES ('Pod A01', 'Single pod, comfortable seat and air ventilation', 'img/podA01.jpg', 'Available', CURDATE(),
            CURDATE(), 1),
           ('Pod A02', 'Single pod with desk lamp and AC', 'img/podA02.jpg', 'Occupied', CURDATE(), CURDATE(), 1),
           ('Meeting Room B01', 'Room with large table and projector', 'img/meetingB01.jpg', 'Available', CURDATE(),
            CURDATE(), 3);

    -- =============================================
-- Fake Data: amenity
-- =============================================
    INSERT INTO `amenity` (`name`, `price`, `quantity`, `type`, `building_id`, `createdAt`, `updatedAt`)
    VALUES ('Espresso', 30000, 50, 'Food', 1, NOW(), NOW()),
           ('Printer Access', 10000, 10, 'Office', 1, NOW(), NOW()),
           ('Projector Rental', 40000, 3, 'Office', 2, NOW(), NOW());

    -- =============================================
-- Fake Data: order
-- =============================================
    INSERT INTO `order` (`id`, `accountId`, `createdAt`, `updatedAt`)
    VALUES (UUID(), (SELECT id FROM account WHERE email = 'a@flexipod.com' LIMIT 1), NOW(), NOW()),
(UUID(), (SELECT id FROM account WHERE email='b@flexipod.com' LIMIT 1), NOW(), NOW());

    -- =============================================
-- Fake Data: orderDetail
-- =============================================
    INSERT INTO `orderDetail` (`id`, `customerId`, `buildingNumber`, `roomId`, `orderId`, `servicePackageId`,
                               `orderHandlerId`, `priceRoom`, `discountPercentage`, `startTime`, `endTime`, `status`,
                               `createdAt`, `updatedAt`)
    VALUES (UUID(),
            (SELECT id FROM account WHERE email = 'a@flexipod.com' LIMIT 1), 1, 1,
           (SELECT id FROM `order` LIMIT 1 OFFSET 0), 1,
           (SELECT id FROM account WHERE email='b@flexipod.com' LIMIT 1), 60000, 0, NOW(), DATE_ADD(NOW(), INTERVAL 1 HOUR), 'Completed', NOW(), NOW() ),
(UUID(),
 (SELECT id FROM account WHERE email='a@flexipod.com' LIMIT 1),
 1,
 2,
 (SELECT id FROM `order` LIMIT 1 OFFSET 1),
 2,
 (SELECT id FROM account WHERE email='c@flexipod.com' LIMIT 1),
 90000,
 10,
 NOW(),
 DATE_ADD(NOW(), INTERVAL 2 HOUR),
 'Pending',
 NOW(),
 NOW()
);

    -- =============================================
-- Fake Data: orderDetailAmenity
-- =============================================
    INSERT INTO `orderDetailAmenity` (`id`, `orderDetailId`, `amenityId`, `quantity`, `price`, `createdAt`, `updatedAt`)
    VALUES (UUID(),
            (SELECT id FROM orderDetail LIMIT 1 OFFSET 0), 1, 1, 30000, NOW(), NOW() ),
(UUID(),
 (SELECT id FROM orderDetail LIMIT 1 OFFSET 1),
 2,
 2,
 20000,
 NOW(),
 NOW()
);

    -- =============================================
-- Fake Data: assignment
-- =============================================
    INSERT INTO `assignment` (`id`, `staffId`, `slot`, `weekDate`)
    VALUES (UUID(), (SELECT id FROM account WHERE email = 'b@flexipod.com' LIMIT 1), 'Morning', '2025-10-20'),
(UUID(), (SELECT id FROM account WHERE email='b@flexipod.com' LIMIT 1), 'Afternoon', '2025-10-21'),
(UUID(), (SELECT id FROM account WHERE email='c@flexipod.com' LIMIT 1), 'Evening', '2025-10-21');

    -- =============================================
-- Fake Data: roomImage
-- =============================================
    INSERT INTO `roomImage` (`roomId`, `imageUrl`, `createdAt`, `updatedAt`)
    VALUES (1, 'img/podA01_1.jpg', NOW(), NOW()),
           (1, 'img/podA01_2.jpg', NOW(), NOW()),
           (3, 'img/meetingB01_1.jpg', NOW(), NOW());

    SELECT '✅ Fake data inserted successfully!' AS Status;
-- =============================================
