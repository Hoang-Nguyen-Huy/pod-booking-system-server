<div align="center">
  <br />
    <a href="https://flexipod.site/" target="_blank">
      <img src="https://flexipod.site/assets/homePageBanner-BtHd3PD-.png" alt="Project Banner">
    </a>
  <br />

<div align="center">
  <img src="https://img.shields.io/badge/-Spring%20Boot-black?style=for-the-badge&logo=springboot&logoColor=white&color=6DB33F" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/-Firebase-black?style=for-the-badge&logo=firebase&logoColor=white&color=FFCA28" alt="Firebase" />
  <img src="https://img.shields.io/badge/-MySQL-black?style=for-the-badge&logo=mysql&logoColor=white&color=4169E1" alt="MySQL" />
  <img src="https://img.shields.io/badge/-Docker-black?style=for-the-badge&logo=docker&logoColor=white&color=2496ED" alt="Docker" />
  <img src="https://img.shields.io/badge/-Nginx%20Proxy-black?style=for-the-badge&logo=nginx&logoColor=white&color=009639" alt="Nginx Proxy" />
</div>


<h3 align="center">FlexiPod</h3>

   <div align="center">
     This project was built for the Pod Booking System.
    </div>
</div>

## 📋 <a name="table">Table of Contents</a>

1. 🤖 [Introduction](#introduction)
2. ⚙️ [Tech Stack](#tech-stack)
3. 🔋 [Features](#features)
4. 🤸 [Quick Start](#quick-start)

## <a name="introduction">🤖 Introduction</a>

Built with React to handle the user interface, Google Calendar to sync booked schedules, VnPay to process payments, MySQL (serverless) to manage databases, and styled with MUI, FlexiPod is a perfect web app. Its main goal is to provide customers with a more convenient way to book spaces.

## <a name="meet-the-team">👥 Meet the Team</a>

FlexiPod was developed by a talented team of five passionate individuals:

<table align="center">
  <tr>
    <td align="center">
      <a href="https://github.com/HuyDiCode" target="_blank" title="Nguyễn Bùi Quốc Huy">
        <img src="https://avatars.githubusercontent.com/u/153421231?v=4" alt="Nguyễn Bùi Quốc Huy" width="100" height="100" style="border-radius: 50%;" />
        <br />
        Nguyễn Bùi Quốc Huy
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/Hoang-Nguyen-Huy" target="_blank" title="Nguyễn Huy Hoàng">
        <img src="https://avatars.githubusercontent.com/u/121879570?v=4" alt="Nguyễn Huy Hoàng" width="100" height="100" style="border-radius: 50%;" />
        <br />
        <strong>Nguyễn Huy Hoàng</strong>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/nguyenhcp2004" target="_blank" title="Huỳnh Chiếm Phương Nguyên">
        <img src="https://avatars.githubusercontent.com/u/140372018?v=4" alt="Huỳnh Chiếm Phương Nguyên" width="100" height="100" style="border-radius: 50%;" />
        <br />
        Huỳnh Chiếm Phương Nguyên (Leader)
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/toki-ai" target="_blank" title="Phạm Thị Anh Đào">
        <img src="https://avatars.githubusercontent.com/u/127603666?v=4" alt="Phạm Thị Anh Đào" width="100" height="100" style="border-radius: 50%;" />
        <br />
        Phạm Thị Anh Đào
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/ThanhTriIsCoding" target="_blank" title="Nguyễn Thanh Trí">
        <img src="https://avatars.githubusercontent.com/u/86301855?v=4" alt="Nguyễn Thanh Trí" width="100" height="100" style="border-radius: 50%;" />
        <br />
        Nguyễn Thanh Trí
      </a>
    </td>
  </tr>
</table>



## <a name="tech-stack">⚙️ Tech Stack</a>

- Spring Boot 
- Firebase
- MySQL
- Docker
- Nginx Proxy

## <a name="features">🔋 Features</a>

👉 **Onboarding Flow**: Seamless user registration and setup process.

👉 **oAuth Using Google**: Easy login using Google credentials.

👉 **Authorization**: Secure access control for different user roles.

👉 **View Room Type**: View a list of room types.

👉 **Book Room**: Book available rooms and amenities.

👉 **Book Amenities**: Book amenities available for the rooms you have reserved.

👉 **Send Google Calendar Invite After Successful Payment**: Send a Google Calendar invite after confirming payment.

👉 **Profile**: View account details in the profile screen.

👉 **History Booking**: Review all rooms booked so far.

👉 **Cancel Booking**: Cancel room booking.

👉 **Manage Order**: Create, and update information of order.

👉 **Manage Order Amenity**: Create, and update information of order amenity.

👉 **Manage Building**: CRUD with building.

👉 **Manage Amenity**: CRUD with amenity.

👉 **Manage account user**: Create, update, and ban accounts in real-time.

👉 **Manage assignment**: The admin or manager can view and assign a shift for staff at the location.

👉 **Responsive on mobile and pc**: Optimized for both mobile and pc devices.

and many more, including code architecture and reusability

## <a name="quick-start">🤸 Quick Start</a>

Follow these steps to set up the project locally on your machine.

**Prerequisites**

Make sure you have the following installed on your machine:

- [Git](https://git-scm.com/)

**Cloning the Repository**

```bash
git clone https://github.com/Hoang-Nguyen-Huy/pod-booking-system-server.git
cd pod-booking-system-server
```

**Set Up Environment Variables**

Create a new file named `application.yaml` in the resources folder of your project and add the following content:

```env
server:
 port: 8080
openapi:
 service:
   api-docs: api-service
   server: http://localhost:8080
   title: API Service
   version: 1.0.0
spring:
 datasource:
   url: "YOUR_DATABASE_URL"
   username: YOUR_DATABASE_USERNAME
   password: YOUR_DATABASE_PASSWORD
 jpa:
   hibernate:
     ddl-auto: update
     naming:
       implicit-strategy: org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl
       physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
   show-sql: true
 mail:
   host: "smtp.gmail.com"
   port: 587
   username: "flexipod.system@gmail.com"
   password: "tbzi kxxk wtqk dztk"
   properties:
     mail:
       smtp:
         auth: true
         starttls:
           enable: true
 security:
   oauth2:
     client:
       registration:
         google:
           client-id: "YOUR_CLIENT_ID"
           client-secret: "YOUR_CLIENT_SECRET"
           scope:
             - email
             - profile
jwt:
 JWT_SECRET_ACCESS_TOKEN: "YOUR_JWT_SECRET_ACCESS_TOKEN"
 JWT_SECRET_REFRESH_TOKEN: "YOUR_JWT_SECRET_REFRESH_TOKEN"
google:
 success: "http://localhost:3000/login/oauth?accessToken="
 failure: "http://localhost:3000/login/oauth?message="
vnpay:
 tmn-code: "YOUR_TMN_CODE"
 hash-secret: "YOUR_HASH_SECRET"
 return-url: "http://localhost:3000/order-detail"
 url: "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html"
```

Replace the placeholder values with your actual credentials. You can send mail for me to get application.yaml for testing.
