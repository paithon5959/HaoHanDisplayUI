<div align="center">

# HaoHanDisplayUI

High-performance engine for creating interactive in-world virtual user interfaces (Virtual UI / 3D Display Canvas) in Minecraft using Display Entities.

[![Minecraft](https://img.shields.io/badge/Minecraft-1.20%2B-5B8C5A?style=for-the-badge&logo=minecraft&logoColor=white)](https://www.minecraft.net/)
[![Release](https://img.shields.io/github/v/release/Hao-Han-SMP/HaoHanDisplayUI?style=for-the-badge&color=2E86AB&label=Release)](https://github.com/Hao-Han-SMP/HaoHanDisplayUI/releases)
[![Paper](https://img.shields.io/badge/Paper-Compatible-1F2421?style=for-the-badge&logo=paper&logoColor=white)](https://papermc.io/)
[![Purpur](https://img.shields.io/badge/Purpur-Supported-8A4FFF?style=for-the-badge)](https://purpurmc.org/)
[![Folia](https://img.shields.io/badge/Folia-Supported-00B4D8?style=for-the-badge)](https://github.com/PaperMC/Folia)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Gradle](https://img.shields.io/badge/Gradle-8.x-02303A?style=for-the-badge&logo=gradle&logoColor=white)](https://gradle.org/)

Language: **English** | [Tiếng Việt](#haohandisplayui-tiếng-việt)

</div>

---

## 1. Demo Video

![HaoHan Display UI demo](media/Demo.gif)

- View or download high-quality video: [media/Demo.mp4](media/Demo.mp4).
- Demonstrates `/hhdui demo` featuring 11 built-in experimental test pages: styled text, paginated lists, sliders, buttons, 2D/3D geometric shapes, live mobs, and continuous gradient panels.

---

## 2. Key Features

- **Layer & Component Architecture**: High-level `LayerManager`, `Layer` (zero Z-fighting depth guarantee), recursive `Container`s with 9-way anchor docking, animated `DropdownContainer`s, and reusable `Component`s (`ButtonComponent`, `CheckboxComponent`, `SliderComponent`, `TextComponent`, `IconComponent`, `ShapeComponent`, `CustomNodeComponent`).
- **Display Entity Canvas**: Full lifecycle management (spawning, rendering, diffing, atomic page replacement via `replace()`, and cleanup) for `TextDisplay`, `ItemDisplay`, `BlockDisplay`, and `Interaction` entities.
- **Pixel-Precise Interaction**: Eye-based raycasting computes exact hit points on the canvas surface, powering interactive buttons, checkboxes, sliders, and scroll lists with rich event payloads.
- **In-Game Diagnostic Suite**: Comprehensive live debugging via `/uidebug` (`inspect`, `clickinspect`, `tree`, `layer`) with real-time targeting BossBar HUD displaying component/container/layer stats.
- **2D/3D Geometry & Gradients**: Supports line segments, triangles, parallelograms, multi-slice gradient panels, live mob entities, and 3D custom models with cursor tracking.
- **Optimized & Multi-Platform Compatible**: Fully compatible with Paper, Purpur, and Folia (multi-threaded region scheduling) from version 1.20 to 1.21.x+.

---

## 3. Installation & Setup Guide

### 3.1 For End-Users (Server Administrators)

1. **System Requirements**:
   - Minecraft server running Paper, Purpur, or Folia (1.20.4 - 1.21.x+).
   - Java 21 runtime environment or higher.
2. **Plugin Installation**:
   - Download the latest plugin JAR from the [Releases](https://github.com/Hao-Han-SMP/HaoHanDisplayUI/releases) page.
   - Place the JAR file into the server's `plugins/` directory.
   - Restart the server.

---

### 3.2 For Contributors & Developers (Building from Source)

#### Step 1: Environment Prerequisites
- Git.
- JDK 21 or higher (Eclipse Temurin or OpenJDK 21 recommended).
- Gradle 8.x (or the bundled `./gradlew` wrapper).

#### Step 2: Clone the Repository
```bash
git clone https://github.com/paithon5959/HaoHanDisplayUI.git
cd HaoHanDisplayUI
```

#### Step 3: Build Instructions
- **Compile Java source files**:
  ```bash
  ./gradlew compileJava
  ```
- **Run automated test suite**:
  ```bash
  ./gradlew test
  ```
- **Package the artifact JAR**:
  ```bash
  ./gradlew build
  ```
  The compiled artifact is generated at: `build/libs/HaoHanDisplayUI-1.0.2.jar`.

- **Publish to local Maven cache**:
  ```bash
  ./gradlew publishToMavenLocal
  ```

#### Step 4: Add as a Project Dependency

- **Gradle (Groovy DSL)**:
  ```groovy
  repositories {
      mavenLocal()
  }

  dependencies {
      compileOnly 'vn.haohan:HaoHanDisplayUI:1.0.2'
  }
  ```

- **Maven (`pom.xml`)**:
  ```xml
  <dependency>
      <groupId>vn.haohan</groupId>
      <artifactId>HaoHanDisplayUI</artifactId>
      <version>1.0.2</version>
      <scope>provided</scope>
  </dependency>
  ```

- **Declaring Dependencies (For Consumer Plugins)**:
  Add to your consumer plugin's `plugin.yml`:
  ```yaml
  depend:
    - HaoHanDisplayUI
  ```

---

## 4. API Documentation

Comprehensive integration guides, method tables, architecture overviews, and code examples are available at:
- Official API Documentation: [API_DOCUMENTATION.md](API_DOCUMENTATION.md)

---

## 5. Contributing Guidelines

All code contributions, bug reports, and feature proposals must follow the repository standards. Please read the guidelines before submitting a Pull Request:
- Contribution Guidelines: [CONTRIBUTING.md](CONTRIBUTING.md)

---

<div align="center">

# HaoHanDisplayUI (Tiếng Việt)

Engine dựng giao diện ảo (Virtual UI / 3D Display Canvas) tương tác trực tiếp trong thế giới Minecraft bằng Display Entities.

[![Minecraft](https://img.shields.io/badge/Minecraft-1.20%2B-5B8C5A?style=for-the-badge&logo=minecraft&logoColor=white)](https://www.minecraft.net/)
[![Release](https://img.shields.io/github/v/release/Hao-Han-SMP/HaoHanDisplayUI?style=for-the-badge&color=2E86AB&label=Phiên%20bản)](https://github.com/Hao-Han-SMP/HaoHanDisplayUI/releases)
[![Paper](https://img.shields.io/badge/Paper-Compatible-1F2421?style=for-the-badge&logo=paper&logoColor=white)](https://papermc.io/)
[![Purpur](https://img.shields.io/badge/Purpur-Supported-8A4FFF?style=for-the-badge)](https://purpurmc.org/)
[![Folia](https://img.shields.io/badge/Folia-Supported-00B4D8?style=for-the-badge)](https://github.com/PaperMC/Folia)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Gradle](https://img.shields.io/badge/Gradle-8.x-02303A?style=for-the-badge&logo=gradle&logoColor=white)](https://gradle.org/)

Ngôn ngữ: [English](#haohandisplayui) | **Tiếng Việt**

</div>

---

## 1. Video Demo

![HaoHan Display UI demo](media/Demo.gif)

- Xem hoặc tải video định dạng chất lượng cao: [media/Demo.mp4](media/Demo.mp4).
- Trình diễn lệnh mẫu `/hhdui demo` với 11 trang giao diện thực nghiệm: văn bản hiệu ứng, danh sách phân trang, thanh trượt slider, nút bấm tương tác, hình học 2D/3D, mob sống và panel dải màu gradient.

---

## 2. Tính Năng Nổi Bật

- **Kiến trúc Layer & Component hiện đại**: Hỗ trợ `LayerManager`, `Layer` chống lỗi Z-fighting, `Container` phân cấp lồng nhau với hệ thống neo 9 điểm, menu thu gọn `DropdownContainer` cùng các `Component` trực quan (`ButtonComponent`, `CheckboxComponent`, `SliderComponent`, `TextComponent`, `IconComponent`, `ShapeComponent`, `CustomNodeComponent`).
- **Dựng UI bằng Display Entity**: Quản lý toàn diện vòng đời spawn, hiển thị, chuyển trang mượt mà không lỗi giật hiển thị (`replace()`) và dọn dẹp các thực thể `TextDisplay`, `ItemDisplay`, `BlockDisplay` và `Interaction`.
- **Tương tác chuẩn xác theo pixel**: Cơ chế raycast từ tầm mắt người chơi tính toán chính xác điểm va chạm trên canvas, hỗ trợ đầy đủ Button, Checkbox, Slider và Scroll List cuộn chuột.
- **Bộ công cụ gỡ lỗi trực tiếp trong game**: Lệnh `/uidebug` hỗ trợ soi thông số trực tiếp (`inspect`), bấm kiểm tra (`clickinspect`), xem cây giao diện (`tree`) cùng thanh BossBar HUD hiển thị thông số component/container/layer trong thời gian thực.
- **Đồ họa 2D/3D và dải màu gradient**: Hỗ trợ vẽ đoạn thẳng, tam giác, hình bình hành, panel gradient đa hướng mượt mà, mob sống và mô hình 3D bám theo con trỏ chuột.
- **Tối ưu hóa và tương thích đa nền tảng**: Tương thích hoàn toàn Paper, Purpur và Folia (hỗ trợ đa luồng vùng region) từ phiên bản 1.20 đến 1.21.x+.

---

## 3. Hướng Dẫn Cài Đặt

### 3.1 Dành Cho Người Dùng (End-User / Server Admin)

1. **Yêu cầu hệ thống**:
   - Server Minecraft chạy Paper, Purpur hoặc Folia (1.20.4 - 1.21.x+).
   - Môi trường chạy Java 21 trở lên.
2. **Cài đặt plugin**:
   - Tải file JAR mới nhất từ trang [Releases](https://github.com/Hao-Han-SMP/HaoHanDisplayUI/releases).
   - Sao chép file JAR vào thư mục `plugins/` của máy chủ.
   - Khởi động lại máy chủ hoặc dùng lệnh nạp plugin.

---

### 3.2 Dành Cho Nhà Phát Triển & Người Đóng Góp (Contributor)

#### Bước 1: Yêu cầu môi trường
- Git.
- JDK 21 trở lên (khuyến nghị Eclipse Temurin hoặc OpenJDK 21).
- Gradle 8.x (hoặc sử dụng Gradle Wrapper `./gradlew` đi kèm dự án).

#### Bước 2: Tải mã nguồn
```bash
git clone https://github.com/paithon5959/HaoHanDisplayUI.git
cd HaoHanDisplayUI
```

#### Bước 3: Biên dịch dự án (Build)
- **Biên dịch mã nguồn Java**:
  ```bash
  ./gradlew compileJava
  ```
- **Chạy bộ kiểm thử tự động (Unit Tests)**:
  ```bash
  ./gradlew test
  ```
- **Đóng gói file JAR hoàn chỉnh**:
  ```bash
  ./gradlew build
  ```
  File thành phẩm nằm tại: `build/libs/HaoHanDisplayUI-1.0.2.jar`.

- **Cài đặt vào kho lưu trữ Maven cục bộ (Local Maven Repository)**:
  ```bash
  ./gradlew publishToMavenLocal
  ```

#### Bước 4: Tích hợp vào dự án khác (Maven / Gradle / plugin.yml)

- **Gradle (Groovy DSL)**:
  ```groovy
  repositories {
      mavenLocal()
  }

  dependencies {
      compileOnly 'vn.haohan:HaoHanDisplayUI:1.0.2'
  }
  ```

- **Maven (`pom.xml`)**:
  ```xml
  <dependency>
      <groupId>vn.haohan</groupId>
      <artifactId>HaoHanDisplayUI</artifactId>
      <version>1.0.2</version>
      <scope>provided</scope>
  </dependency>
  ```

- **Khai báo phụ thuộc (dành cho plugin phụ thuộc)**:
  Thêm vào file `plugin.yml` của plugin muốn dùng thư viện:
  ```yaml
  depend:
    - HaoHanDisplayUI
  ```

---

## 4. Tài Liệu API (API Documentation)

Toàn bộ hướng dẫn tích hợp, bảng tra cứu method, giải thích kiến trúc và ví dụ code mẫu được lưu trữ chi tiết tại:
- Tài liệu API chính thức: [API_DOCUMENTATION.md](API_DOCUMENTATION.md)

---

## 5. Quy Định Đóng Góp (Contributing)

Mọi đóng góp mã nguồn, báo lỗi hoặc đề xuất tính năng mới cần tuân theo quy chuẩn chung của dự án. Vui lòng đọc kỹ hướng dẫn trước khi gửi Pull Request:
- Quy định đóng góp: [CONTRIBUTING.md](CONTRIBUTING.md)
