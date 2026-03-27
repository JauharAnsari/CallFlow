# 📱 CallFlow – Real Calling App

## 📌 Overview
CallFlow is a **real calling Android application** built using Kotlin and Jetpack Compose.  
This app demonstrates the use of **Android’s native telecom features**, including placing real phone calls, accessing contacts, and reading call logs.

Unlike simulated apps, CallFlow interacts directly with the device’s **actual calling capabilities and system data**.

---

## 🎯 Objective
The goal of this project is to implement a **fully functional calling app** that can:
- Place real phone calls
- Access device contacts
- Display real call logs
- Show live call duration

---

## ✨ Features

### 🔢 Dial Pad Screen
- Numeric keypad (0–9, *, #)
- Input number display
- Backspace/delete functionality
- Call button to place **real phone calls**

### 📞 Real Calling Functionality
- Uses `Intent.ACTION_CALL`
- Initiates actual phone calls from the device
- Handles runtime permissions properly

### 📋 Call Logs
- Fetches real call logs using `CallLog.Calls`
- Displays:
  - Contact name (if available)
  - Phone number
  - Call type (Incoming / Outgoing / Missed)
  - Date & time
  - Call duration
- Tap any log → call that number again

### 👤 Contacts Integration
- Fetches real contacts using `ContactsContract`
- Displays contact name and number
- Tap a contact → initiate real call

### ⏱️ Live Call Duration
- Detects call state using system services
- Displays real-time call duration
- Updates UI dynamically during calls

---

## 🔄 Call Handling Flow

The app listens to real device call states and updates UI accordingly.

---

## 🔐 Permissions

The app properly handles runtime permissions:

- `CALL_PHONE`
- `READ_CONTACTS`
- `READ_CALL_LOG`

✔ Requests permissions at runtime  
✔ Handles denied cases gracefully  

---

## ⚙️ Tech Stack

- **Language:** Kotlin  
- **UI:** Jetpack Compose  
- **Framework:** Android SDK  
- **Architecture:** MVVM (Model-View-ViewModel)  
- **Navigation:** Compose Navigation  

---

## 🧠 Architecture

The app follows **MVVM architecture** to ensure:
- Clean and maintainable code  
- Separation of concerns  
- Efficient state management  
- Scalable project structure  

---

## 🚫 Important Note

> This app uses **real Android calling functionality**.  
> ✔ No fake or simulated call flows  
> ✔ Works with actual device data (contacts & call logs)  
> ✔ Demonstrates real-world Android development skills  

---

## 📸 Screenshots

<table align="center">
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/30d1708d-9378-41e0-8247-eec9aa039ad7" width="220"/><br/>
      <b>Dial Pad</b>
    </td>

    <td align="center">
      <img src="https://github.com/user-attachments/assets/1a297280-c7d9-4661-a68a-bb698ddd7b0e" width="220"/><br/>
      <b>Incoming Call</b>
    </td>

    <td align="center">
      <img src="https://github.com/user-attachments/assets/964f8395-326b-4811-a149-0b7387932c92" width="220"/><br/>
      <b>Active Call</b>
    </td>
  </tr>
</table>

---

## 📦 APK Download

👉 [Download APK](https://drive.google.com/file/d/14CQB_ujuqWeevnOoG70cb9WhYEurrFeT/view?usp=drivesdk)

---

## 🛠 Installation Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/JauharAnsari/CallFlow.git
