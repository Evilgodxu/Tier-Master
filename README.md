# 梯度大师 (Tier List Maker)

一款专业的梯度表/排行榜生成工具，支持批量图片管理、自定义层级、主题切换等功能。

## 📱 应用预览

<p align="center">
  <img src="docs/screenshot.webp" alt="梯度大师界面预览" width="300"/>
</p>

## 功能特性

- 📊 **梯度表生成**：轻松创建专业的梯度表/排行榜
- 🖼️ **图片管理**：支持批量导入、拖拽排序、裁切编辑
- 🎨 **自定义层级**：自由添加、删除、重命名层级，自定义颜色
- 🏷️ **小图标系统**：支持为图片添加最多3个小图标
- 📦 **图包管理**：支持导入ZIP格式图包（支持密码保护）
- 💾 **预设管理**：保存、导入、导出梯度表配置
- 🌙 **主题切换**：支持浅色/深色主题，可跟随系统
- 🌍 **多语言支持**：支持多种语言切换

## 技术栈

- **语言**：Kotlin
- **UI框架**：Jetpack Compose + Material Design 3
- **架构**：MVVM + Repository
- **最低SDK**：31 (Android 12)
- **目标SDK**：36

## 项目结构

```
app/src/main/java/com/tdds/jh/
├── bitmap/          # 位图生成相关
├── domain/          # 业务逻辑层
│   └── utils/       # 工具类
├── manager/         # 管理器类
├── preset/          # 预设管理
├── resource/        # 资源管理
├── ui/              # UI层
│   ├── dialog/      # 对话框组件
│   ├── theme/       # 主题相关
│   └── toast/       # Toast提示
├── util/            # 通用工具
└── MainActivity.kt  # 主Activity
```

## 构建要求

- Android Studio Ladybug 或更高版本
- JDK 17
- Gradle 9.3.1+

## 构建说明

1. 克隆仓库
```bash
git clone <repository-url>
```

2. 使用 Android Studio 打开项目

3. 同步 Gradle 依赖

4. 构建测试版项目
```bash
./gradlew assembleDebug
```
5. 检查依赖更新
```bash
./gradlew refreshVersions
```
6. 检查 Kotlin 格式化
```bash
.\gradlew.bat ktlintCheck
```
7. 检查 Android 构建
```bash
.\gradlew.bat lintDebug
```

## 📥 下载安装

### 最新版本：v1.0.0

[**点击下载 APK 安装包**](https://github.com/Evilgodxu/Tier-Master/releases/download/v1.0.0/梯度大师_1.0.0.apk)

> **提示**：安装时请允许"未知来源应用"安装权限

---

## ☕ 打赏支持

如果这个项目对你有帮助，欢迎请作者喝杯咖啡！你的支持是我持续开发的动力~

<table>
  <tr>
    <td align="center">
      <img src="docs/alipay.webp" alt="支付宝" width="250"/><br/>
      <b>支付宝</b>
    </td>
    <td align="center">
      <img src="docs/wechat-pay.webp" alt="微信支付" width="250"/><br/>
      <b>微信支付</b>
    </td>
  </tr>
</table>

---

## 开源协议

本项目采用GPL-3.0开源协议发布，欢迎自由使用和修改。

## 贡献指南

欢迎提交 Issue 和 Pull Request 参与项目贡献。

## 致谢

感谢所有为这个项目做出贡献的开发者。
