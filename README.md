# 🚗 Car Rental Web Application

This project is a **web-based car rental application** developed as part of an **educational exercise** to learn about software development using **Spring Boot** and **Thymeleaf**. The aim is to explore modern web development practices while building a feature-rich application.

## 📋 Table of Contents
- [About the Project](#about-the-project)
- [Features](#features)
- [Technical Stack](#technical-stack)
- [Setup Instructions](#setup-instructions)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)
- [Project Showcase](#project-showcase)

## 🎯 About the Project

The **Car Rental Web Application** provides an efficient platform for managing car rentals. It allows users to browse vehicles, make reservations, and handle payments seamlessly. This project was built to deepen understanding of the **Spring MVC design pattern**, backend development with **Spring Boot**, and frontend integration with **Thymeleaf**.

## 🌟 Features

### 🔑 User Management
- User registration and login (clients and administrators)
- Profile management: update personal information like name, address, and phone number

### 🚗 Vehicle Management
- Administrators can add, update, or delete vehicle details
- Users can view available vehicles with detailed information:
  - Brand
  - Model
  - Year
  - Type
  - Rental Price

### 🗓️ Vehicle Reservation
- Search for available vehicles using filters (date, type, etc.)
- Make reservations online by selecting rental dates
- Receive reservation confirmation and rental contracts
- Track vehicle status: available, reserved, or under maintenance

### 💳 Payment and Billing
- Calculate rental costs based on daily rates, duration, and additional fees
- Generate invoices and process payments online

### 📊 Reporting and Analytics
- Generate weekly or monthly reports on reservations, vehicle usage, and revenue

## 🛠️ Technical Stack

- **Backend**: Spring Boot (using the Spring MVC design pattern)
- **Frontend**: Thymeleaf
- **Database**: MySQL or PostgreSQL
- **Application Server**: Apache Tomcat (embedded with Spring Boot)
- **Development Tools**: IntelliJ IDEA / Eclipse, Maven
- **Modeling Tools**: UML (Unified Modeling Language), StarUML

## ⚙️ Setup Instructions

1. **Clone the repository**:
   ```bash
   git clone git@github.com:sperkoo/carRental.git
   ```

2. **Import the project**:
   - Open the project in your IDE (e.g., IntelliJ IDEA or Eclipse)

3. **Configure the database**:
   - Create a MySQL or PostgreSQL database
   - Update the database connection details in `src/main/resources/application.properties`

4. **Build and run the application**:
   ```bash
   # Build the project
   mvn clean install

   # Run the application
   mvn spring-boot:run
   ```

5. **Access the application**:
   - Open your browser and navigate to `http://localhost:8080`

## 🚀 Usage

### Administrators Usage
- Check the directory Admin_Usage

### Clients Usage
- Check the directory Client_Usage

## 🤝 Contributing

We welcome contributions to enhance this project! To contribute:

1. Fork the repository
2. Create a feature branch:
   ```bash
   git checkout -b feature-name
   ```
3. Commit your changes:
   ```bash
   git commit -m "Add feature description"
   ```
4. Push to your branch:
   ```bash
   git push origin feature-name
   ```
5. Submit a pull request

## 📜 License

This project is licensed for educational purposes. Feel free to use it for learning and inspiration. For reuse or adaptations, please provide appropriate credit.

---

⭐ Made with passion and caffeine
