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
