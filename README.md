# Infra Automation

โปรเจกต์นี้เป็น **Infrastructure Automation** สำหรับจัดการ Cloud Infrastructure โดยใช้  
**Jenkins Shared Library + Terraform**  
เพื่อให้การสร้าง ใช้ และทำลาย Infrastructure เป็นไปอย่างอัตโนมัติ
คลิปสาธิต: https://youtu.be/FxB_tIKX_XQ?si=h9dkOaduDhdu-Lbs
## Overview
Infra Automation นี้มี **3 Pipelines หลัก**

### 1. Request Pipeline
คัดลอก **Terraform Template กลาง** มาไว้ที่ **Repository ของโปรเจค**  
เพื่อเตรียมพร้อมสำหรับการสร้าง Infrastructure

### 2. Provision Pipeline
สร้าง Infrastructure ผ่าน **Terraform modules**  ที่ถูกคัดลอกมา โดยใช้ `terraform init / plan / apply`

### 3. Destroy Pipeline
ทำลาย Infrastructure โดยอ้างอิงจาก **Terraform State (tfstate)**  ที่เก็บไว้บน **Azure**
