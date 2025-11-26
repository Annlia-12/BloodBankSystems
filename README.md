
# **🌟 Kerala Hospital Blood Bank Management System**

A powerful **Java Swing + MySQL** desktop application designed for hospitals across Kerala to manage blood donors, recipients, blood stock, donation history, requests, notifications, and statewide blood donation camps — all from a secure, hospital-centric system.

---

# **📌 Overview**

This system allows hospitals to efficiently handle all blood bank operations in one place.
Each hospital gets its own authenticated workspace, complete with donor/recipient data, real-time blood stock, automated updates, and event listings.

---

# **✨ Key Features**

### **✔️ Hospital-Centric System**

* Each hospital gets its own secure account
* Fully isolated records for donors, recipients, requests, and stock

### **✔️ Authentication**

* Hospital signup
* Login with secure credential validation
* Auto-refresh dropdown of available hospitals

### **✔️ Donor Management**

* Add new donors
* Search by name
* Filter by blood group
* Record donations (auto-updates blood stock + history + notifications)

### **✔️ Recipient (Patient) Management**

* Add patients who require blood
* Record medical reasons, contact info, blood group, and more

### **✔️ Blood Stock Management**

* Real-time stock per hospital
* Auto-initialization of 8 essential blood groups
* Status indicators: Good / Low / Critical

### **✔️ Blood Request System**

* Hospitals can raise blood requests for recipients
* Auto-approval if stock is sufficient
* Auto-pending if stock is low
* Auto deduction of units when approved
* Notification on every request

### **✔️ Donation History**

* Complete chronological log of all donations
* Sorted by latest first
* Includes donor names, units, dates, and blood groups

### **✔️ Notifications Center**

* All major events (donations, requests, approvals)
* Mark unread → read automatically when viewed

### **✔️ Kerala Blood Camps Listing**

* Statewide upcoming blood donation events
* Includes district, location, date, contact, and descriptions

---

# **🛠 Tech Stack**

**Language:** Java (Swing GUI)
**Database:** MySQL
**Connectivity:** JDBC
**Architecture:** MVC-inspired layered desktop application

---

# **📁 Project Structure (Main Screens & Classes)**

### **🔹 Core System & Auth**

* **BloodBankDB.java** — Database connection helper
* **HospitalLoginScreen.java** — Hospital login panel
* **HospitalSignUpScreen.java** — New hospital registration
* **HospitalMainMenu.java** — Dashboard after login

### **🔹 Donor & Recipient Operations**

* **AddDonorScreen.java** — Add new donors
* **DonorListScreen.java** — List, filter, search donors + record donation
* **AddRecipientScreen.java** — Register new patients

### **🔹 Blood Handling**

* **RequestBloodScreen.java** — Raise blood requests
* **ViewStockScreen.java** — Current blood stock with color statuses
* **DonationHistoryScreen.java** — Full donation log

### **🔹 Additional Modules**

* **NotificationsScreen.java** — All event updates
* **BloodCampsScreen.java** — Statewide blood donation camps

---

# **🗄 Database Design (Clean & Copy-Safe)**

Each table is created with `CREATE TABLE IF NOT EXISTS` to avoid errors.

### **🏥 hospitals**

* id (PK)
* hospital_name (unique)
* district
* location
* contact
* email
* password
* created_at

### **🩸 blood_stock**

* id (PK)
* hospital_id (FK)
* blood_group
* units
* last_updated

### **🧑‍🩺 donors**

* id (PK)
* hospital_id (FK)
* name
* blood_group
* age
* gender
* contact
* address
* health_issues
* last_donation_date
* created_at

### **👥 recipients**

* id (PK)
* hospital_id (FK)
* name
* blood_group
* age
* contact
* reason
* created_at

### **📘 donation_history**

* id (PK)
* hospital_id (FK)
* donor_id (FK)
* donor_name
* blood_group
* units
* donation_date

### **📨 blood_requests**

* id (PK)
* hospital_id (FK)
* recipient_id (FK, nullable)
* recipient_name
* blood_group
* units
* reason
* status (Approved / Pending)
* request_date
* approved_date (nullable)

### **🔔 notifications**

* id (PK)
* hospital_id (FK)
* type
* message
* status (Unread / Read)
* created_at

### **📅 blood_camps**

* id (PK)
* name
* district
* location
* event_date
* contact
* description
* created_at

---

# **🔄 Key Application Flows (Detailed)**

### **1️⃣ Hospital Signup**

* Hospital fills in name, district, contact, password
* Record saved into `hospitals`
* System auto-creates **8 stock entries**: A+, A-, B+, B-, AB+, AB-, O+, O-
* Login dropdown refreshes instantly

### **2️⃣ Hospital Login**

* Selects hospital name from dropdown
* Enters password
* Validated using DB query
* Loads hospital’s dashboard (hospital_id passed to every screen)

### **3️⃣ Donor Management**

#### AddDonorScreen:

* Validates fields (age, contact, empty fields)
* Saves donor to DB

#### DonorListScreen:

* Loads donors for the logged-in hospital
* Search by name
* Filter by blood group

#### Record Donation:

* Ask units donated
* Insert into donation_history
* Update blood_stock (+ units)
* Update donor last_donation_date
* Add notification entry

### **4️⃣ Recipient Registration**

* Stores patient details (name, blood group, reason)
* Saves into recipients table
* Used for blood requests

### **5️⃣ Blood Request System**

* Select recipient
* Choose blood group + units
* System checks stock:

  * If enough → **Approved** and units deducted
  * If low → **Pending**
* Record inserted into blood_requests
* Notification created

### **6️⃣ Blood Stock View**

* Loads all blood_group → units for the hospital
* If missing, auto-initializes
* Shows colored statuses (green/yellow/red)

### **7️⃣ Donation History**

* Records sorted by latest date
* Shows all donors, units, and dates

### **8️⃣ Notifications**

* Lists all notifications
* Mark unread → read after viewing

### **9️⃣ Blood Camps**

* Shows upcoming events across Kerala (event_date ≥ today)

---

# **📦 Setup & Run**

### **Prerequisites**

* Java JDK 8+
* MySQL Server
* MySQL Connector/J
* IntelliJ / Eclipse / NetBeans

### **1️⃣ Clone the Repository**

```bash
git clone https://github.com/your-username/kerala-blood-bank-system.git
cd kerala-blood-bank-system
```

### **2️⃣ Import into IDE**

* Open your IDE
* Import as Java project

### **3️⃣ Add JDBC Connector**

* Add `mysql-connector-j.jar` to libraries

### **4️⃣ Configure Database Credentials**

```java
private static final String URL = "jdbc:mysql://localhost:3306/blood_bank_kerala";
private static final String USER = "root";
private static final String PASS = "";
```

### **5️⃣ Run**

* Start from **HospitalLoginScreen.java → main()**

---

# **🏗 MVC Architecture Overview**

### **Model**

* MySQL database
* JDBC operations (SELECT, INSERT, UPDATE, DELETE)

### **View**

* Java Swing components
* JFrames, JPanels, JTables, Forms

### **Controller**

* ActionListener events
* Button click logic
* Data validation
* DB interaction handlers

---

# **🚀 Future Improvements**

### **✨ Security Upgrades**

* Password hashing (bcrypt)
* OTP-based hospital verification

### **✨ Feature Enhancements**

* Donor eligibility rule (90-day gap)
* Advanced stock analytics per month
* PDF/Excel export of reports
* Live notifications across hospitals
* SMS/Email alerts to donors
* Multi-role accounts (Admin / Staff / Nurse)


