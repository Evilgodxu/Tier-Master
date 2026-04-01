package com.tdds.jh

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast

import java.io.File
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background

import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberDraggableState
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import androidx.compose.runtime.key
import androidx.compose.runtime.DisposableEffect
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import kotlin.math.abs
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import com.tdds.jh.ui.theme.LocalExtendedColors
import com.tdds.jh.ui.theme.ThemeManager
import com.tdds.jh.resource.ResourceManager
import com.tdds.jh.resource.PackageManager as ResourcePackageManager
import com.tdds.jh.resource.PackageItem
import com.tdds.jh.resource.ImportTarget
import com.tdds.jh.resource.ZipPasswordRequiredException
import com.tdds.jh.manager.ImageResourceManager
import com.tdds.jh.preset.ManagePresetsDialog
import com.tdds.jh.preset.PresetNameDialog
import com.tdds.jh.preset.PresetListDialog
import com.tdds.jh.preset.LoadingDialog
import com.tdds.jh.preset.ImportOverwriteDialog
import com.tdds.jh.preset.DraftRestoreDialog
import com.tdds.jh.bitmap.TierImage
import com.tdds.jh.bitmap.TierItem
import com.tdds.jh.bitmap.generateTierListBitmap

import com.tdds.jh.ui.dialog.AboutDialog
import com.tdds.jh.ui.dialog.PreviewDialog

import com.tdds.jh.ui.dialog.edit.ColorPickerDialog
import com.tdds.jh.ui.dialog.edit.EditAuthorDialog
import com.tdds.jh.ui.dialog.edit.EditTierNameDialog
import com.tdds.jh.ui.dialog.edit.EditTitleDialog
import com.tdds.jh.ui.dialog.settings.ProgramSettingsDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowDropDown
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.FilterChip
import androidx.compose.ui.graphics.BlendMode
import java.io.FileOutputStream
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.LocalTextStyle
import androidx.activity.compose.BackHandler
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat

import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.CachePolicy
import com.tdds.jh.ui.theme.MyApplicationTheme
import com.tdds.jh.ui.toast.ToastHost
import com.tdds.jh.ui.toast.showToastWithoutIcon
import com.tdds.jh.domain.utils.TextUtils
import com.tdds.jh.domain.utils.ColorUtils
import com.tdds.jh.domain.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.util.UUID
import kotlin.math.roundToInt

// 手势类型枚举
private enum class GestureType {
    LongPress,      // 长按
    VerticalDrag,   // 垂直拖动
    HorizontalSwipe // 水平滑动
}

class MainActivity : ComponentActivity() {

    // 草稿保存回调，用于在双击退出时触发保存
    private var saveDraftCallback: (() -> Unit)? = null

    // 标记是否正在执行不需要保存草稿的操作（如打开图片选择器、文件选择器等）
    // 当此标记为 true 时，onUserLeaveHint 不会触发草稿保存
    private var isSkippingDraftSave = false

    // 临时禁用草稿保存，用于执行特定操作时
    private fun skipDraftSaveTemporarily() {
        isSkippingDraftSave = true
        AppLogger.d("临时禁用草稿保存")
    }

    // 恢复草稿保存
    private fun resumeDraftSave() {
        isSkippingDraftSave = false
        AppLogger.d("恢复草稿保存")
    }

    override fun onDestroy() {
        AppLogger.i("MainActivity onDestroy - 开始清理资源")
        saveDraftCallback = null

        AppLogger.i("MainActivity onDestroy - 资源清理完成")
        super.onDestroy()
    }

    override fun onTrimMemory(level: Int) {
        AppLogger.i("MainActivity onTrimMemory - 级别: $level")

        // 根据内存压力级别采取相应措施
        // 注意：从 Android API 34 开始，只有部分级别仍然有效
        when {
            // UI 隐藏时 (20) - 记录日志即可
            level == 20 -> {
                AppLogger.i("UI不可见")
            }
            // 应用进入后台 (40) - 记录日志即可
            level >= 40 -> {
                AppLogger.i("应用进入后台")
            }
            // 运行内存低 (10) - 记录日志即可
            // 注意：此级别在 API 34+ 可能不再收到通知
            level >= 10 -> {
                AppLogger.i("运行内存低")
            }
        }
        super.onTrimMemory(level)
    }

    override fun onLowMemory() {
        AppLogger.w("MainActivity onLowMemory - 系统内存不足")
        super.onLowMemory()
    }

    override fun attachBaseContext(newBase: android.content.Context) {
        // 读取保存的语言设置
        val prefs = newBase.getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
        // 检查是否是首次启动
        val isFirstLaunch = prefs.getBoolean("is_first_launch", true)
        val language = if (isFirstLaunch) {
            // 首次启动：根据系统语言自动设置
            val systemLocale = newBase.resources.configuration.locales[0]
            val systemLanguage = systemLocale.language
            // 支持中文、英文、日语、韩语、俄语、德语、法语、西班牙语、阿拉伯语、葡萄牙语，其他语言默认使用中文
            val autoLanguage = when (systemLanguage) {
                "zh" -> "zh"
                "en" -> "en"
                "ja" -> "ja"
                "ko" -> "ko"
                "ru" -> "ru"
                "de" -> "de"
                "fr" -> "fr"
                "es" -> "es"
                "ar" -> "ar"
                "pt" -> "pt"
                else -> "zh"
            }
            // 保存自动检测的语言设置，并标记需要显示语言选择对话框
            prefs.edit()
                .putString("language", autoLanguage)
                .putBoolean("is_first_launch", false)
                .putBoolean("show_language_on_first_launch", true)
                .apply()
            autoLanguage
        } else {
            prefs.getString("language", "zh") ?: "zh"
        }
        // 创建带新语言的 Context
        val locale = java.util.Locale.forLanguageTag(language)
        val config = android.content.res.Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初始化日志系统，传入版本信息
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val versionName = packageInfo.versionName ?: ""
        val versionCode = packageInfo.longVersionCode.toInt()
        AppLogger.init(this, versionName, versionCode)
        AppLogger.i("MainActivity onCreate")

        // 记录启动前存储状态
        AppLogger.markOperation("应用启动")
        AppLogger.logStorageUsage(this, "启动前")

        // 应用启动时清理所有临时资源
        val presetManager = PresetManager(this)
        presetManager.cleanupAllCache()
        AppLogger.i("应用启动 - 已清理所有缓存")

        // 记录清理后存储状态
        AppLogger.logStorageUsage(this, "清理缓存后")

        enableEdgeToEdge()
        setContent {
            // 使用主题管理器管理主题状态
            val themeState = ThemeManager.rememberThemeState(this)
            val isDarkTheme = themeState.value.isDarkTheme
            val systemInDarkTheme = ThemeManager.getSystemInDarkTheme()
            
            // 禁用字体开关：开启时禁用自定义字体(默认启用)
            val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            var disableCustomFont by remember { mutableStateOf(prefs.getBoolean("disable_custom_font", true)) }

            // 应用状态栏主题
            ThemeManager.ApplyStatusBarTheme(isDarkTheme)

            MyApplicationTheme(
                darkTheme = isDarkTheme,
                disableCustomFont = disableCustomFont
            ) {
                TierListMakerApp(
                    isDarkTheme = isDarkTheme,
                    followSystemTheme = themeState.value.followSystemTheme,
                    disableCustomFont = disableCustomFont,
                    onDisableCustomFontChange = { newValue ->
                        disableCustomFont = newValue
                        prefs.edit().putBoolean("disable_custom_font", newValue).apply()
                        AppLogger.i("设置 禁用字体: $newValue")
                    },
                    onThemeChange = { newTheme ->
                        val newState = ThemeManager.toggleTheme(this, themeState.value)
                        themeState.value = newState
                        AppLogger.i("保存主题设置: ${if (newTheme) "深色" else "浅色"}")
                    },
                    onFollowSystemThemeChange = { newValue ->
                        val newState = ThemeManager.setFollowSystemTheme(this, newValue, systemInDarkTheme)
                        themeState.value = newState
                    },
                    onRegisterSaveDraftCallback = { callback ->
                        saveDraftCallback = callback
                    },
                    onSaveDraftForResourceManager = {
                        saveDraftCallback?.invoke()
                    },
                    onSkipDraftSave = {
                        skipDraftSaveTemporarily()
                    },
                    onResumeDraftSave = {
                        resumeDraftSave()
                    }
                )
            }
        }
    }

    /**
     * 双击退出时立即返回桌面，同步保存草稿，后台清理资源
     * 工作流程：返回桌面 -> 同步保存草稿 -> 后台清理 -> 结束程序
     */
    fun exitAppWithCleanup() {
        AppLogger.i("双击退出 - 立即返回桌面")

        // 立即返回桌面，不阻塞UI
        finishAffinity()

        // 同步保存草稿（阻塞直到完成，确保草稿保存成功）
        AppLogger.i("双击退出 - 开始同步保存草稿")
        try {
            saveDraftCallback?.invoke()
            AppLogger.i("双击退出 - 草稿保存完成")
        } catch (e: Exception) {
            AppLogger.e("双击退出 - 保存草稿失败", e)
        }

        // 在后台协程中执行清理操作
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val presetManager = PresetManager(this@MainActivity)
                presetManager.cleanupAllCache()
                AppLogger.i("双击退出 - 所有资源清理完成")
            } catch (e: Exception) {
                AppLogger.e("双击退出 - 清理资源失败", e)
            }

            AppLogger.i("双击退出 - 后台操作全部完成")
        }
    }

    /**
     * 当用户离开Activity时调用（如按Home键、切换到其他应用）
     * 注意：对话框不会触发此方法
     */
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()

        // 检查是否需要跳过草稿保存
        if (isSkippingDraftSave) {
            AppLogger.d("onUserLeaveHint - 跳过草稿保存（正在执行特定操作）")
            return
        }

        AppLogger.i("onUserLeaveHint - 用户离开应用，触发草稿保存")
        // 使用生命周期协程作用域确保保存完成
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    saveDraftCallback?.invoke()
                }
                AppLogger.i("onUserLeaveHint - 草稿保存完成")
            } catch (e: Exception) {
                AppLogger.e("onUserLeaveHint - 保存草稿失败", e)
            }
        }
    }

}

// UI尺寸配置 - 基于460dp基准宽度进行适配
object UiDimensions {
    const val BASE_SCREEN_WIDTH = 460f
    
    @Composable
    fun getScaleFactor(): Float {
        val configuration = LocalContext.current.resources.configuration
        val screenWidth = configuration.screenWidthDp.dp
        return (screenWidth.value / BASE_SCREEN_WIDTH).coerceIn(0.78f, 1f) // 最小缩放至78%(360dp)
    }
    
    // 基础尺寸
    val TIER_LABEL_WIDTH = 70f
    val IMAGE_SIZE = 70f
    val TIER_ROW_HEIGHT = 80f
    val LABEL_FONT_SIZE = 20f
    val TIER_LABEL_CORNER_RADIUS = 8f
    val IMAGE_CORNER_RADIUS = 4f
    val BADGE_SIZE_RATIO = 0.22f
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TierListMakerApp(
    isDarkTheme: Boolean = false,
    followSystemTheme: Boolean = true,
    disableCustomFont: Boolean = false,
    onDisableCustomFontChange: ((Boolean) -> Unit)? = null,
    onThemeChange: (Boolean) -> Unit = {},
    onFollowSystemThemeChange: ((Boolean) -> Unit)? = null,
    onRegisterSaveDraftCallback: ((() -> Unit) -> Unit)? = null,
    onSaveDraftForResourceManager: (() -> Unit)? = null,
    onSkipDraftSave: (() -> Unit)? = null,
    onResumeDraftSave: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val scope = rememberCoroutineScope()
    
    // 获取屏幕适配缩放因子
    val scaleFactor = UiDimensions.getScaleFactor()
    
    // 计算适配后的尺寸
    val tierLabelWidth = (UiDimensions.TIER_LABEL_WIDTH * scaleFactor).dp
    val imageSize = (UiDimensions.IMAGE_SIZE * scaleFactor).dp
    val tierRowHeight = (UiDimensions.TIER_ROW_HEIGHT * scaleFactor).dp
    val labelFontSize = (UiDimensions.LABEL_FONT_SIZE * scaleFactor).sp
    val tierLabelCornerRadius = (UiDimensions.TIER_LABEL_CORNER_RADIUS * scaleFactor).dp
    val imageCornerRadius = (UiDimensions.IMAGE_CORNER_RADIUS * scaleFactor).dp

    // 记录应用启动日志
    LaunchedEffect(Unit) {
        AppLogger.i("TierListMakerApp 启动")
        AppLogger.i("系统版本: Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
        AppLogger.i("设备: ${Build.MANUFACTURER} ${Build.MODEL}")
        AppLogger.i("屏幕缩放因子: $scaleFactor")
    }

    // 默认梯度模板 - 根据语言选择
    val currentLocale = context.resources.configuration.locales[0]
    val isChinese = currentLocale.language == "zh"
    
    val defaultTiers = if (isChinese) {
        // 中文本地化模板
        listOf(
            TierItem("夯", Color(0xFFFF6B6B)),
            TierItem("顶级", Color(0xFFFFB347)),
            TierItem("人上人", Color(0xFFFFFACD)),
            TierItem("NPC", Color(0xFFB8E6B8)),
            TierItem("拉完了", Color(0xFF87CEEB)),
            TierItem("路边一条", Color(0xFFDDA0DD))
        )
    } else {
        // 其他语言使用标准模板
        listOf(
            TierItem("S", Color(0xFFFF6B6B)),
            TierItem("A", Color(0xFFFFB347)),
            TierItem("B", Color(0xFFFFFACD)),
            TierItem("C", Color(0xFFB8E6B8)),
            TierItem("D", Color(0xFF87CEEB)),
            TierItem("E", Color(0xFFDDA0DD))
        )
    }

    // 当前梯度列表
    val tiers = remember { mutableStateListOf<TierItem>().apply { addAll(defaultTiers) } }

    // 每个梯度的图片
    val tierImages = remember { mutableStateListOf<TierImage>() }

    // 设置菜单状态
    var showSettingsMenu by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showInstructionsDialog by remember { mutableStateOf(false) }
    var showImportPackageDialog by remember { mutableStateOf(false) }

    var showProgramSettingsDialog by remember { mutableStateOf(false) }
    var showResourceManageDialog by remember { mutableStateOf(false) }


    // 程序设置状态
    val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    var disableClickAdd by remember { mutableStateOf(prefs.getBoolean("disable_click_add", true)) }
    // 调节浮显：水平偏移 0-300dp（默认125），垂直偏移 0-150dp（默认85）
    var floatOffsetX by remember { mutableStateOf(prefs.getFloat("float_offset_x", 125f)) }
    var floatOffsetY by remember { mutableStateOf(prefs.getFloat("float_offset_y", 85f)) }
    // 外置小图开关：启用时小图标显示在图片右侧
    var externalBadgeEnabled by remember { mutableStateOf(prefs.getBoolean("external_badge_enabled", false)) }
    // 下置命名开关：启用时图片命名显示在图片下方
    var nameBelowImage by remember { mutableStateOf(prefs.getBoolean("name_below_image", false)) }

    // 待添加的图片
    var pendingImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    
    // ZIP导入的待添加图片
    var zipPendingImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var showZipPasswordDialog by remember { mutableStateOf(false) }
    var pendingZipUri by remember { mutableStateOf<Uri?>(null) }
    
    // 图包管理状态
    var showManagePackagesDialog by remember { mutableStateOf(false) }
    var showPackageConfirmDialog by remember { mutableStateOf(false) }
    var selectedPackage by remember { mutableStateOf<PackageItem.Imported?>(null) }
    var selectedPackageImageCount by remember { mutableStateOf(0) }
    var isImportingPackage by remember { mutableStateOf(false) }  // 防止重复导入
    var isExportingPackage by remember { mutableStateOf(false) }  // 图包导出状态
    var packageToExport by remember { mutableStateOf<PackageItem.Imported?>(null) }  // 待导出的图包

    // 标题状态
    var tierListTitle by remember { mutableStateOf(context.getString(R.string.default_title)) }
    var showEditTitleDialog by remember { mutableStateOf(false) }

    // 作者信息状态
    var authorName by remember { mutableStateOf("") }
    var showEditAuthorDialog by remember { mutableStateOf(false) }

    // 语言设置状态 - 从 SharedPreferences 读取（复用上面定义的 prefs）
    val savedLanguage = prefs.getString("language", "zh") ?: "zh"
    // 检查是否需要显示首次启动的语言选择对话框
    val shouldShowLanguageOnFirstLaunch = prefs.getBoolean("show_language_on_first_launch", true)
    var currentLanguage by remember { mutableStateOf(savedLanguage) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var languageChanged by remember { mutableStateOf(false) }

    // 语言切换 - 使用 recreate 重启 Activity 来应用新语言
    LaunchedEffect(languageChanged) {
        if (languageChanged) {
            languageChanged = false
            // 保存语言设置到 SharedPreferences
            context.getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
                .edit()
                .putString("language", currentLanguage)
                .apply()
            // 重启 Activity 以应用新语言
            (context as ComponentActivity).recreate()
        }
    }

    // 对话框状态
    var editingTier by remember { mutableStateOf<TierItem?>(null) }
    var showEditNameDialog by remember { mutableStateOf(false) }
    var showColorPickerDialog by remember { mutableStateOf(false) }
    var selectedImageForAction by remember { mutableStateOf<TierImage?>(null) }
    var showImageActionDialog by remember { mutableStateOf(false) }
    var imageToReplace by remember { mutableStateOf<TierImage?>(null) }
    var showMoveImageDialog by remember { mutableStateOf(false) }
    var showEditImageNameDialog by remember { mutableStateOf(false) }
    var showImageViewDialog by remember { mutableStateOf(false) }
    var showCropDialog by remember { mutableStateOf(false) }
    var imageForBadge by remember { mutableStateOf<TierImage?>(null) }  // 要添加小图标的图片
    var showSetBadgeDialog by remember { mutableStateOf(false) }  // 设置小图标对话框
    var badgeSelectionTarget by remember { mutableStateOf(0) }  // 0=无, 1=小图标1, 2=小图标2
    var badgeDialogRefreshKey by remember { mutableStateOf(0) }  // 用于强制刷新对话框
    var isBadgePickerLaunching by remember { mutableStateOf(false) }  // 防止重复打开图片选择器
    var isImagePickerLaunching by remember { mutableStateOf(false) }  // 防止重复打开层级图片选择器
    var isResetting by remember { mutableStateOf(false) }  // 防止重复点击重置按钮

    // 拖拽选中状态
    var selectedImageForDrag by remember { mutableStateOf<TierImage?>(null) }
    
    // 层级图片拖拽状态
    var draggingTierImage by remember { mutableStateOf<TierImage?>(null) }
    var draggingTierImagePosition by remember { mutableStateOf(Offset.Zero) }
    var draggingTierImageTarget by remember { mutableStateOf<String?>(null) }
    var isDraggingTierImageDeleteMode by remember { mutableStateOf(false) }
    var isDraggingTierImageToPending by remember { mutableStateOf(false) }
    
    // 待分级区位置
    var pendingSectionRect by remember { mutableStateOf<android.graphics.Rect?>(null) }

    // 层级位置跟踪（用于拖放）- 必须在 presetFilePicker 之前定义
    var tierRowPositions by remember { mutableStateOf<Map<String, android.graphics.Rect>>(emptyMap()) }

    // 删除层级对话框状态
    var showDeleteTierDialog by remember { mutableStateOf(false) }
    var tierToDelete by remember { mutableStateOf<TierItem?>(null) }

    // 图片预览对话框状态
    var showPreviewDialog by remember { mutableStateOf(false) }
    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var previewIsDarkTheme by remember { mutableStateOf(false) }
    var isSavingChart by remember { mutableStateOf(false) }
    var isSharingChart by remember { mutableStateOf(false) }

    // 预设管理器
    val presetManager = remember { PresetManager(context) }

    // 预设管理对话框状态
    var showManagePresetsDialog by remember { mutableStateOf(false) }
    var showPresetNameDialog by remember { mutableStateOf(false) }
    var showPresetListDialog by remember { mutableStateOf(false) }
    var showPresetOverwriteConfirmDialog by remember { mutableStateOf(false) }
    var pendingPresetName by remember { mutableStateOf("") }
    var isExportingPreset by remember { mutableStateOf(false) }
    var isImportingPreset by remember { mutableStateOf(false) }
    var isSavingPreset by remember { mutableStateOf(false) }
    var presetOperation by remember { mutableStateOf<PresetOperation?>(null) }

    // 导入预设覆盖确认对话框状态
    var showImportOverwriteDialog by remember { mutableStateOf(false) }
    var pendingImportResult by remember { mutableStateOf<PresetManager.ImportResult?>(null) }

    // 草稿恢复对话框状态
    var showDraftRestoreDialog by remember { mutableStateOf(false) }
    // 只存储草稿配置数据，不解压图片，等待用户确认后再解压
    var draftConfigData by remember { mutableStateOf<PresetData?>(null) }
    // 草稿加载中对话框状态
    var showDraftLoadingDialog by remember { mutableStateOf(false) }
    // 是否跳过草稿恢复（用于外部导入预设时）
    var skipDraftRestore by remember { mutableStateOf(false) }

    // 检查是否是从外部打开 .tdds 或 .zip 文件
    LaunchedEffect(Unit) {
        val activity = context as? ComponentActivity
        activity?.intent?.let { intent ->
            // 处理 VIEW 动作（文件管理器等）
            if (intent.action == Intent.ACTION_VIEW) {
                val dataUri = intent.data
                if (dataUri != null) {
                    // 使用 FileUtils 获取文件名来检查扩展名
                    val fileName = FileUtils.getFileNameFromUri(context, dataUri)
                    val uriString = dataUri.toString()
                    val isTddsFile = (fileName?.endsWith(".tdds", ignoreCase = true) == true) ||
                            uriString.endsWith(".tdds", ignoreCase = true) ||
                            uriString.contains(".tdds", ignoreCase = true)
                    val isZipFile = (fileName?.endsWith(".zip", ignoreCase = true) == true) ||
                            uriString.endsWith(".zip", ignoreCase = true) ||
                            uriString.contains(".zip", ignoreCase = true)
                    
                    if (isTddsFile) {
                        AppLogger.i("从外部打开 .tdds 文件 (VIEW): $dataUri, fileName: $fileName")
                        isImportingPreset = true
                        skipDraftRestore = true
                        try {
                            val importResult = withContext(Dispatchers.IO) {
                                presetManager.importPreset(dataUri)
                            }
                            when (importResult.status) {
                                PresetManager.ImportStatus.SUCCESS,
                                PresetManager.ImportStatus.ALREADY_EXISTS -> {
                                    val result = withContext(Dispatchers.IO) {
                                        presetManager.applyPreset(importResult.presetFile)
                                    }
                                    tiers.clear()
                                    tiers.addAll(result.tiers.map { tierData ->
                                        TierItem(tierData.label, try {
                                            Color(android.graphics.Color.parseColor("#${tierData.color}"))
                                        } catch (e: Exception) { Color.Gray })
                                    })
                                    tierImages.removeAll { true }
                                    result.tierImages.forEach { appliedImage ->
                                        tierImages.add(TierImage(
                                            id = appliedImage.id,
                                            tierLabel = appliedImage.tierLabel,
                                            uri = appliedImage.uri,
                                            name = appliedImage.name,
                                            badgeUri = appliedImage.badgeUri,
                                            badgeUri2 = appliedImage.badgeUri2,
                                            badgeUri3 = appliedImage.badgeUri3,
                                            originalUri = appliedImage.originalUri,
                                            cropPositionX = appliedImage.cropPositionX,
                                            cropPositionY = appliedImage.cropPositionY,
                                            cropScale = appliedImage.cropScale,
                                            isCropped = appliedImage.isCropped,
                                            cropRatio = appliedImage.cropRatio,
                                            useCustomCrop = appliedImage.useCustomCrop,
                                            customCropWidth = appliedImage.customCropWidth,
                                            customCropHeight = appliedImage.customCropHeight
                                        ))
                                    }
                                    pendingImages = result.pendingImages
                                    tierListTitle = result.title
                                    authorName = result.author
                                    prefs.edit()
                                        .putFloat("crop_position_x", result.cropPositionX)
                                        .putFloat("crop_position_y", result.cropPositionY)
                                        .putInt("custom_crop_width", result.customCropWidth)
                                        .putInt("custom_crop_height", result.customCropHeight)
                                        .putBoolean("use_custom_crop_size", result.useCustomCropSize)
                                        .putFloat("crop_ratio", result.cropRatio)
                                        .apply()
                                    // 清理草稿文件（保留工作目录中的图片）
                                    presetManager.cleanupDraftOnly()
                                    showToastWithoutIcon(
                                        context,
                                        context.getString(R.string.preset_import_success),
                                        Toast.LENGTH_SHORT
                                    )
                                    AppLogger.i("外部打开 .tdds 文件并应用预设成功: ${result.title}")
                                }
                                PresetManager.ImportStatus.NEEDS_OVERWRITE -> {
                                    pendingImportResult = importResult
                                    showImportOverwriteDialog = true
                                    AppLogger.i("外部打开 .tdds 文件需要覆盖确认")
                                }
                            }
                        } catch (e: Exception) {
                            AppLogger.e("外部打开 .tdds 文件失败", e)
                            showToastWithoutIcon(
                                context,
                                context.getString(R.string.preset_import_failed, e.message),
                                Toast.LENGTH_SHORT
                            )
                        } finally {
                            isImportingPreset = false
                        }
                        // 清除 intent 避免重复处理
                        activity.intent = null
                        return@LaunchedEffect
                    }
                    
                    // 处理 .zip 图包文件
                    if (isZipFile) {
                        AppLogger.i("从外部打开 .zip 图包文件 (VIEW): $dataUri, fileName: $fileName")
                        isImportingPreset = true
                        skipDraftRestore = true
                        try {
                            // 保存图包到图包目录
                            val savedPackageFile = withContext(Dispatchers.IO) {
                                presetManager.saveImportedPackage(dataUri, fileName ?: "imported_${System.currentTimeMillis()}.zip")
                            }
                            if (savedPackageFile != null) {
                                AppLogger.i("外部图包导入成功: ${savedPackageFile.name}")
                                showToastWithoutIcon(
                                    context,
                                    context.getString(R.string.package_import_success, savedPackageFile.nameWithoutExtension),
                                    Toast.LENGTH_SHORT
                                )
                            } else {
                                AppLogger.e("外部图包导入失败: 保存文件返回null")
                                showToastWithoutIcon(
                                    context,
                                    context.getString(R.string.package_import_failed),
                                    Toast.LENGTH_SHORT
                                )
                            }
                        } catch (e: Exception) {
                            AppLogger.e("外部打开 .zip 图包文件失败", e)
                            showToastWithoutIcon(
                                context,
                                context.getString(R.string.package_import_failed, e.message),
                                Toast.LENGTH_SHORT
                            )
                        } finally {
                            isImportingPreset = false
                        }
                        // 清除 intent 避免重复处理
                        activity.intent = null
                        return@LaunchedEffect
                    }
                }
            }

            // 处理 SEND 动作（QQ等应用分享）
            if (intent.action == Intent.ACTION_SEND || intent.action == Intent.ACTION_SEND_MULTIPLE) {
                val dataUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(Intent.EXTRA_STREAM)
                }
                if (dataUri != null) {
                    // 使用 FileUtils 获取文件名来检查扩展名
                    val fileName = FileUtils.getFileNameFromUri(context, dataUri)
                    val uriString = dataUri.toString()
                    val isTddsFile = (fileName?.endsWith(".tdds", ignoreCase = true) == true) ||
                            uriString.endsWith(".tdds", ignoreCase = true) ||
                            uriString.contains(".tdds", ignoreCase = true)
                    val isZipFile = (fileName?.endsWith(".zip", ignoreCase = true) == true) ||
                            uriString.endsWith(".zip", ignoreCase = true) ||
                            uriString.contains(".zip", ignoreCase = true)
                    
                    if (isTddsFile) {
                        AppLogger.i("从外部分享打开 .tdds 文件 (SEND): $dataUri, fileName: $fileName")
                        isImportingPreset = true
                        skipDraftRestore = true
                        try {
                            val importResult = withContext(Dispatchers.IO) {
                                presetManager.importPreset(dataUri)
                            }
                            when (importResult.status) {
                                PresetManager.ImportStatus.SUCCESS,
                                PresetManager.ImportStatus.ALREADY_EXISTS -> {
                                    val result = withContext(Dispatchers.IO) {
                                        presetManager.applyPreset(importResult.presetFile)
                                    }
                                    tiers.clear()
                                    tiers.addAll(result.tiers.map { tierData ->
                                        TierItem(tierData.label, try {
                                            Color(android.graphics.Color.parseColor("#${tierData.color}"))
                                        } catch (e: Exception) { Color.Gray })
                                    })
                                    tierImages.removeAll { true }
                                    result.tierImages.forEach { appliedImage ->
                                        tierImages.add(TierImage(
                                            id = appliedImage.id,
                                            tierLabel = appliedImage.tierLabel,
                                            uri = appliedImage.uri,
                                            name = appliedImage.name,
                                            badgeUri = appliedImage.badgeUri,
                                            badgeUri2 = appliedImage.badgeUri2,
                                            badgeUri3 = appliedImage.badgeUri3,
                                            originalUri = appliedImage.originalUri,
                                            cropPositionX = appliedImage.cropPositionX,
                                            cropPositionY = appliedImage.cropPositionY,
                                            cropScale = appliedImage.cropScale,
                                            isCropped = appliedImage.isCropped,
                                            cropRatio = appliedImage.cropRatio,
                                            useCustomCrop = appliedImage.useCustomCrop,
                                            customCropWidth = appliedImage.customCropWidth,
                                            customCropHeight = appliedImage.customCropHeight
                                        ))
                                    }
                                    pendingImages = result.pendingImages
                                    tierListTitle = result.title
                                    authorName = result.author
                                    prefs.edit()
                                        .putFloat("crop_position_x", result.cropPositionX)
                                        .putFloat("crop_position_y", result.cropPositionY)
                                        .putInt("custom_crop_width", result.customCropWidth)
                                        .putInt("custom_crop_height", result.customCropHeight)
                                        .putBoolean("use_custom_crop_size", result.useCustomCropSize)
                                        .putFloat("crop_ratio", result.cropRatio)
                                        .apply()
                                    // 清理草稿文件（保留工作目录中的图片）
                                    presetManager.cleanupDraftOnly()
                                    showToastWithoutIcon(
                                        context,
                                        context.getString(R.string.preset_import_success),
                                        Toast.LENGTH_SHORT
                                    )
                                    AppLogger.i("外部分享打开 .tdds 文件并应用预设成功: ${result.title}")
                                }
                                PresetManager.ImportStatus.NEEDS_OVERWRITE -> {
                                    pendingImportResult = importResult
                                    showImportOverwriteDialog = true
                                    AppLogger.i("外部分享打开 .tdds 文件需要覆盖确认")
                                }
                            }
                        } catch (e: Exception) {
                            AppLogger.e("外部分享打开 .tdds 文件失败", e)
                            showToastWithoutIcon(
                                context,
                                context.getString(R.string.preset_import_failed, e.message),
                                Toast.LENGTH_SHORT
                            )
                        } finally {
                            isImportingPreset = false
                        }
                        // 清除 intent 避免重复处理
                        activity.intent = null
                        return@LaunchedEffect
                    }
                    
                    // 处理 .zip 图包文件
                    if (isZipFile) {
                        AppLogger.i("从外部分享打开 .zip 图包文件 (SEND): $dataUri, fileName: $fileName")
                        isImportingPreset = true
                        skipDraftRestore = true
                        try {
                            // 保存图包到图包目录
                            val savedPackageFile = withContext(Dispatchers.IO) {
                                presetManager.saveImportedPackage(dataUri, fileName ?: "imported_${System.currentTimeMillis()}.zip")
                            }
                            if (savedPackageFile != null) {
                                AppLogger.i("外部图包导入成功: ${savedPackageFile.name}")
                                showToastWithoutIcon(
                                    context,
                                    context.getString(R.string.package_import_success, savedPackageFile.nameWithoutExtension),
                                    Toast.LENGTH_SHORT
                                )
                            } else {
                                AppLogger.e("外部图包导入失败: 保存文件返回null")
                                showToastWithoutIcon(
                                    context,
                                    context.getString(R.string.package_import_failed),
                                    Toast.LENGTH_SHORT
                                )
                            }
                        } catch (e: Exception) {
                            AppLogger.e("外部分享打开 .zip 图包文件失败", e)
                            showToastWithoutIcon(
                                context,
                                context.getString(R.string.package_import_failed, e.message),
                                Toast.LENGTH_SHORT
                            )
                        } finally {
                            isImportingPreset = false
                        }
                        // 清除 intent 避免重复处理
                        activity.intent = null
                        return@LaunchedEffect
                    }
                }
            }
        }
    }

    // 检查是否存在草稿（仅在当前没有编辑内容时显示恢复对话框）
    LaunchedEffect(Unit) {
        // 如果跳过草稿恢复标志已设置，直接返回
        if (skipDraftRestore) {
            AppLogger.i("跳过草稿恢复检查（从外部导入预设或图包）")
            return@LaunchedEffect
        }
        if (presetManager.hasDraft()) {
            // 检查当前是否已经有编辑内容
            val hasCurrentContent = tierListTitle != context.getString(R.string.default_title) ||
                    authorName.isNotEmpty() ||
                    tierImages.isNotEmpty() ||
                    pendingImages.isNotEmpty() ||
                    tiers.size != defaultTiers.size ||
                    tiers.zip(defaultTiers).any { (current, default) ->
                        current.label != default.label || current.color != default.color
                    }

            // 只有当前没有编辑内容时才显示恢复对话框
            if (!hasCurrentContent) {
                // 只读取草稿配置，不解压图片（等待用户确认后再解压）
                val draftConfig = presetManager.readDraftConfig()
                if (draftConfig != null) {
                    draftConfigData = draftConfig
                    showDraftRestoreDialog = true
                    AppLogger.i("发现草稿且当前无编辑内容，显示恢复对话框（未解压）")
                }
            } else {
                AppLogger.i("发现草稿但当前已有编辑内容，跳过恢复对话框")
                // 清理草稿，因为用户正在编辑新内容
                presetManager.cleanupDraft()
            }
        }
    }

    // 注册草稿保存回调到 Activity
    DisposableEffect(Unit) {
        onRegisterSaveDraftCallback?.invoke {
            // 检查是否有编辑内容（非默认状态）
            val hasContent = tierListTitle != context.getString(R.string.default_title) ||
                    authorName.isNotEmpty() ||
                    tierImages.isNotEmpty() ||
                    pendingImages.isNotEmpty() ||
                    tiers.size != defaultTiers.size ||
                    tiers.zip(defaultTiers).any { (current, default) ->
                        current.label != default.label || current.color != default.color
                    }

            if (hasContent) {
                // 保存草稿 - 使用 runBlocking 确保保存完成
                // 注意：不进行任何文件复制操作，直接打包工作目录中的现有文件
                kotlinx.coroutines.runBlocking {
                    try {
                        AppLogger.markOperation("保存草稿")
                        AppLogger.logStorageUsage(context, "保存草稿前")
                        val presetData = presetManager.createPresetData(
                            title = tierListTitle,
                            author = authorName,
                            tiers = tiers,
                            tierImages = tierImages,
                            pendingImages = pendingImages,
                            cropPositionX = prefs.getFloat("crop_position_x", 0.5f),
                            cropPositionY = prefs.getFloat("crop_position_y", 0.5f),
                            customCropWidth = prefs.getInt("custom_crop_width", 0),
                            customCropHeight = prefs.getInt("custom_crop_height", 0),
                            useCustomCropSize = prefs.getBoolean("use_custom_crop_size", false),
                            cropRatio = prefs.getFloat("crop_ratio", 1f)
                        )
                        presetManager.saveDraft(presetData)
                        AppLogger.i("双击退出时保存草稿: $tierListTitle")
                        AppLogger.logStorageUsage(context, "保存草稿后")
                    } catch (e: Exception) {
                        AppLogger.e("保存草稿失败", e)
                    }
                }
            } else {
                // 没有内容，清理草稿
                presetManager.cleanupDraft()
                AppLogger.i("没有编辑内容，清理草稿")
            }
        }
        onDispose { }
    }

    // 预设文件选择器
    val presetFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // 先设置状态显示加载对话框
            isImportingPreset = true
            scope.launch {
                // 让出时间片，确保UI有时间显示加载对话框
                yield()
                AppLogger.markOperation("导入预设")
                AppLogger.logStorageUsage(context, "导入预设前")
                try {
                    // 在后台线程执行耗时操作
                    val importResult = withContext(Dispatchers.IO) {
                        presetManager.importPreset(it)
                    }
                    when (importResult.status) {
                        PresetManager.ImportStatus.SUCCESS -> {
                            // 导入成功，应用预设数据
                            val result = withContext(Dispatchers.IO) {
                                presetManager.applyPreset(importResult.presetFile)
                            }
                            // 更新UI状态
                            tiers.clear()
                            tiers.addAll(result.tiers.map { tierData ->
                                TierItem(tierData.label, try {
                                    Color(android.graphics.Color.parseColor("#${tierData.color}"))
                                } catch (e: Exception) { Color.Gray })
                            })
                            // 强制清除所有图片数据
                            tierImages.removeAll { true }
                            // 添加新的图片数据
                            result.tierImages.forEach { appliedImage ->
                                tierImages.add(TierImage(
                                    id = appliedImage.id,
                                    tierLabel = appliedImage.tierLabel,
                                    uri = appliedImage.uri,
                                    name = appliedImage.name,
                                    badgeUri = appliedImage.badgeUri,
                                    badgeUri2 = appliedImage.badgeUri2,
                                    badgeUri3 = appliedImage.badgeUri3,
                                    originalUri = appliedImage.originalUri,
                                    cropPositionX = appliedImage.cropPositionX,
                                    cropPositionY = appliedImage.cropPositionY,
                                    cropScale = appliedImage.cropScale,
                                    isCropped = appliedImage.isCropped,
                                    cropRatio = appliedImage.cropRatio,
                                    useCustomCrop = appliedImage.useCustomCrop,
                                    customCropWidth = appliedImage.customCropWidth,
                                    customCropHeight = appliedImage.customCropHeight
                                ))
                            }
                            pendingImages = result.pendingImages
                            tierListTitle = importResult.presetData.title
                            authorName = importResult.presetData.author
                            // 清理旧的裁剪设置并应用新的
                            prefs.edit()
                                .remove("crop_ratio")
                                .remove("custom_crop_width")
                                .remove("custom_crop_height")
                                .remove("use_custom_crop_size")
                                .putInt("custom_crop_width", result.customCropWidth)
                                .putInt("custom_crop_height", result.customCropHeight)
                                .putBoolean("use_custom_crop_size", result.useCustomCropSize)
                                .putFloat("crop_ratio", result.cropRatio)
                                .apply()
                            // 清空层级位置信息,确保使用预设中的层级标签
                            tierRowPositions = emptyMap()
                            showToastWithoutIcon(context, context.getString(R.string.preset_import_success))
                            AppLogger.i("导入预设成功: ${importResult.presetData.title}")
                            AppLogger.logStorageUsage(context, "导入预设后")

                            // 静默保存覆盖原预设文件（转换为WebP格式）
                            withContext(Dispatchers.IO) {
                                try {
                                    val workImagesDir = presetManager.getWorkImagesDirectory()
                                    val newPresetData = presetManager.createPresetData(
                                        title = tierListTitle,
                                        author = authorName,
                                        tiers = tiers,
                                        tierImages = tierImages,
                                        pendingImages = pendingImages,
                                        cropPositionX = prefs.getFloat("crop_position_x", 0.5f),
                                        cropPositionY = prefs.getFloat("crop_position_y", 0.5f),
                                        customCropWidth = prefs.getInt("custom_crop_width", 0),
                                        customCropHeight = prefs.getInt("custom_crop_height", 0),
                                        useCustomCropSize = prefs.getBoolean("use_custom_crop_size", false),
                                        cropRatio = prefs.getFloat("crop_ratio", 1f)
                                    )
                                    presetManager.exportPreset(
                                        presetName = tierListTitle,
                                        presetData = newPresetData,
                                        tempDir = workImagesDir,
                                        outputFile = importResult.presetFile
                                    )
                                    AppLogger.i("静默覆盖预设成功: ${importResult.presetFile.name}")
                                } catch (e: Exception) {
                                    AppLogger.e("静默覆盖预设失败", e)
                                }
                            }
                        }
                        PresetManager.ImportStatus.NEEDS_OVERWRITE -> {
                            // 需要询问是否覆盖
                            isImportingPreset = false
                            pendingImportResult = importResult
                            showImportOverwriteDialog = true
                            AppLogger.i("导入预设需要覆盖确认: ${importResult.presetData.title}")
                        }
                        PresetManager.ImportStatus.ALREADY_EXISTS -> {
                            // 完全相同的预设已存在
                            isImportingPreset = false
                            val result = withContext(Dispatchers.IO) {
                                presetManager.applyPreset(importResult.presetFile)
                            }
                            // 更新UI状态
                            tiers.clear()
                            tiers.addAll(result.tiers.map { tierData ->
                                TierItem(tierData.label, try {
                                    Color(android.graphics.Color.parseColor("#${tierData.color}"))
                                } catch (e: Exception) { Color.Gray })
                            })
                            tierImages.clear()
                            tierImages.addAll(result.tierImages.map { appliedImage ->
                                TierImage(
                                    id = appliedImage.id,
                                    tierLabel = appliedImage.tierLabel,
                                    uri = appliedImage.uri,
                                    name = appliedImage.name,
                                    badgeUri = appliedImage.badgeUri,
                                    badgeUri2 = appliedImage.badgeUri2,
                                    badgeUri3 = appliedImage.badgeUri3,
                                    originalUri = appliedImage.originalUri,
                                    cropPositionX = appliedImage.cropPositionX,
                                    cropPositionY = appliedImage.cropPositionY,
                                    cropScale = appliedImage.cropScale,
                                    isCropped = appliedImage.isCropped,
                                    cropRatio = appliedImage.cropRatio,
                                    useCustomCrop = appliedImage.useCustomCrop,
                                    customCropWidth = appliedImage.customCropWidth,
                                    customCropHeight = appliedImage.customCropHeight
                                )
                            })
                            pendingImages = result.pendingImages
                            tierListTitle = importResult.presetData.title
                            authorName = importResult.presetData.author
                            // 清理旧的裁剪设置并应用新的
                            prefs.edit()
                                .remove("crop_ratio")
                                .remove("custom_crop_width")
                                .remove("custom_crop_height")
                                .remove("use_custom_crop_size")
                                .putInt("custom_crop_width", result.customCropWidth)
                                .putInt("custom_crop_height", result.customCropHeight)
                                .putBoolean("use_custom_crop_size", result.useCustomCropSize)
                                .putFloat("crop_ratio", result.cropRatio)
                                .apply()
                            // 清空层级位置信息,确保使用预设中的层级标签
                            tierRowPositions = emptyMap()
                            showToastWithoutIcon(context, context.getString(R.string.preset_already_loaded))
                            AppLogger.i("加载已存在的预设: ${importResult.presetData.title}")
                        }
                    }
                } catch (e: Exception) {
                    AppLogger.e("导入预设失败", e)
                    showToastWithoutIcon(
                        context,
                        context.getString(R.string.preset_import_failed, e.message),
                        Toast.LENGTH_LONG
                    )
                }
                isImportingPreset = false
                // 恢复草稿保存
                onResumeDraftSave?.invoke()
            }
        } ?: run {
            // 用户取消了选择，恢复草稿保存
            onResumeDraftSave?.invoke()
        }
    }

    // 预设导出文件创建器
    val presetExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        uri?.let {
            // 先设置状态显示加载对话框
            isExportingPreset = true
            scope.launch {
                // 让出时间片，确保UI有时间显示加载对话框
                yield()
                AppLogger.markOperation("导出预设")
                AppLogger.logStorageUsage(context, "导出预设前")
                try {
                    // 在后台线程执行耗时操作
                    val presetData = withContext(Dispatchers.IO) {
                        presetManager.createPresetData(
                            title = tierListTitle,
                            author = authorName,
                            tiers = tiers,
                            tierImages = tierImages,
                            pendingImages = pendingImages,
                            cropPositionX = prefs.getFloat("crop_position_x", 0.5f),
                            cropPositionY = prefs.getFloat("crop_position_y", 0.5f),
                            customCropWidth = prefs.getInt("custom_crop_width", 0),
                            customCropHeight = prefs.getInt("custom_crop_height", 0),
                            useCustomCropSize = prefs.getBoolean("use_custom_crop_size", false),
                            cropRatio = prefs.getFloat("crop_ratio", 1f)
                        )
                    }
                    val outputFile = File(context.cacheDir, "${pendingPresetName}.tdds")
                    withContext(Dispatchers.IO) {
                        presetManager.exportPreset(presetData, outputFile)

                        // 复制到用户选择的位置
                        // 使用 "rwt" 模式确保可以覆盖已存在的文件
                        context.contentResolver.openOutputStream(uri, "rwt")?.use { output ->
                            java.io.FileInputStream(outputFile).use { input ->
                                input.copyTo(output)
                            }
                        }
                        outputFile.delete()
                    }

                    showToastWithoutIcon(context, context.getString(R.string.preset_export_success))
                    AppLogger.i("导出预设成功: $pendingPresetName")
                    AppLogger.logStorageUsage(context, "导出预设后")
                } catch (e: Exception) {
                    AppLogger.e("导出预设失败", e)
                    showToastWithoutIcon(
                        context,
                        context.getString(R.string.preset_export_failed, e.message),
                        Toast.LENGTH_LONG
                    )
                }
                isExportingPreset = false
                pendingPresetName = ""
                // 恢复草稿保存
                onResumeDraftSave?.invoke()
            }
        } ?: run {
            // 用户取消了选择，恢复草稿保存
            onResumeDraftSave?.invoke()
        }
    }

    // 图包导出文件创建器
    val packageExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        uri?.let { outputUri ->
            packageToExport?.let { packageItem ->
                isExportingPackage = true
                scope.launch {
                    AppLogger.markOperation("导出图包")
                    AppLogger.logStorageUsage(context, "导出图包前")
                    try {
                        val success = ResourcePackageManager.exportPackageAsWebP(
                            context,
                            packageItem.file,
                            outputUri
                        )
                        if (success) {
                            showToastWithoutIcon(context, context.getString(R.string.package_export_success))
                            AppLogger.i("导出图包成功: ${packageItem.name}")
                        } else {
                            showToastWithoutIcon(
                                context,
                                context.getString(R.string.package_export_failed),
                                Toast.LENGTH_LONG
                            )
                        }
                        AppLogger.logStorageUsage(context, "导出图包后")
                    } catch (e: Exception) {
                        AppLogger.e("导出图包失败", e)
                        showToastWithoutIcon(
                            context,
                            context.getString(R.string.package_export_failed, e.message),
                            Toast.LENGTH_LONG
                        )
                    }
                    isExportingPackage = false
                    packageToExport = null
                }
            }
        }
    }

    // 待添加图片拖动状态
    var isDraggingPendingImage by remember { mutableStateOf(false) }
    var draggedPendingImageUri by remember { mutableStateOf<Uri?>(null) }

    // 图片选择器（多选）- 支持WebP转换和哈希查重
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 50)
    ) { uris ->
        if (uris.isNotEmpty()) {
            AppLogger.markOperation("选择图片")
            AppLogger.logStorageUsage(context, "选择图片前")

            // 在协程中处理图片导入（支持WebP转换和哈希查重）
            scope.launch {
                val workImagesDir = presetManager.getWorkImagesDirectory()
                val imagesDir = File(workImagesDir, "images")
                imagesDir.mkdirs()

                // 构建已有文件的哈希映射表
                val existingHashes = mutableMapOf<String, File>()
                imagesDir.listFiles()?.forEach { file ->
                    if (file.isFile) {
                        try {
                            val hash = ImageResourceManager.calculateQuickHash(file)
                            existingHashes[hash] = file
                        } catch (e: Exception) {
                            // 忽略计算失败的文件
                        }
                    }
                }

                val importedUris = mutableListOf<Uri>()
                var convertedCount = 0
                var reusedCount = 0

                uris.forEach { uri ->
                    val result = ImageResourceManager.importImageWithWebPAndHash(
                        context,
                        uri,
                        imagesDir,
                        existingHashes,
                        convertToWebP = true
                    )

                    if (result.fileName != null) {
                        val file = File(imagesDir, result.fileName)
                        importedUris.add(Uri.fromFile(file))
                        if (result.isConvertedToWebP) convertedCount++
                        if (result.isReused) reusedCount++
                    }
                }

                pendingImages = importedUris
                AppLogger.i("选择图片: ${importedUris.size} 张 (WebP转换: $convertedCount, 复用: $reusedCount)")
                AppLogger.logStorageUsage(context, "选择图片后")

                // 重置防重复点击状态
                isImagePickerLaunching = false
                // 恢复草稿保存
                onResumeDraftSave?.invoke()
            }
        } else {
            // 重置防重复点击状态
            isImagePickerLaunching = false
            // 恢复草稿保存
            onResumeDraftSave?.invoke()
        }
    }

    // 图片选择器（多选，用于添加到待分级区域）- 支持WebP转换和哈希查重
    val addToPendingPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 50)
    ) { uris ->
        if (uris.isNotEmpty()) {
            AppLogger.markOperation("添加图片")
            AppLogger.logStorageUsage(context, "添加图片前")

            // 在协程中处理图片导入（支持WebP转换和哈希查重）
            scope.launch {
                val workImagesDir = presetManager.getWorkImagesDirectory()
                val imagesDir = File(workImagesDir, "images")
                imagesDir.mkdirs()

                // 构建已有文件的哈希映射表（包括现有的pendingImages）
                val existingHashes = mutableMapOf<String, File>()
                imagesDir.listFiles()?.forEach { file ->
                    if (file.isFile) {
                        try {
                            val hash = ImageResourceManager.calculateQuickHash(file)
                            existingHashes[hash] = file
                        } catch (e: Exception) {
                            // 忽略计算失败的文件
                        }
                    }
                }

                val importedUris = mutableListOf<Uri>()
                var convertedCount = 0
                var reusedCount = 0

                uris.forEach { uri ->
                    // 检查URI是否已经在pendingImages中（简单URI去重）
                    if (uri in pendingImages) {
                        reusedCount++
                        return@forEach
                    }

                    val result = ImageResourceManager.importImageWithWebPAndHash(
                        context,
                        uri,
                        imagesDir,
                        existingHashes,
                        convertToWebP = true
                    )

                    if (result.fileName != null) {
                        val file = File(imagesDir, result.fileName)
                        importedUris.add(Uri.fromFile(file))
                        if (result.isConvertedToWebP) convertedCount++
                        if (result.isReused) reusedCount++
                    }
                }

                // 追加到现有待分级图片中
                pendingImages = pendingImages + importedUris
                AppLogger.i("添加图片到待分级区域: ${importedUris.size} 张 (WebP转换: $convertedCount, 复用: $reusedCount)，现有 ${pendingImages.size} 张")
                AppLogger.logStorageUsage(context, "添加图片后")

                // 显示添加成功提示
                showToastWithoutIcon(context, context.getString(R.string.images_added, importedUris.size))

                // 重置防重复点击状态
                isImagePickerLaunching = false
                // 恢复草稿保存
                onResumeDraftSave?.invoke()
            }
        } else {
            // 重置防重复点击状态
            isImagePickerLaunching = false
            // 恢复草稿保存
            onResumeDraftSave?.invoke()
        }
    }

    // 图片选择器（单选，用于替换）
    val replaceImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null && imageToReplace != null) {
            val index = tierImages.indexOfFirst { it.id == imageToReplace!!.id }
            if (index != -1) {
                val oldImage = tierImages[index]
                
                // 在协程中处理文件复制
                scope.launch {
                    try {
                        // 将新图片复制到工作目录
                        val workImagesDir = File(presetManager.getWorkImagesDirectory(), PresetManager.IMAGES_FOLDER_NAME)
                        workImagesDir.mkdirs()
                        
                        // 生成新文件名
                        val newFileName = "replaced_${System.currentTimeMillis()}.webp"
                        val destFile = File(workImagesDir, newFileName)
                        
                        // 复制图片到工作目录
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            FileOutputStream(destFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                        
                        val newUri = Uri.fromFile(destFile)
                        
                        // 返回原图到待分级区域
                        val uriToReturn = oldImage.originalUri ?: oldImage.uri
                        if (uriToReturn !in pendingImages) {
                            pendingImages = pendingImages + uriToReturn
                        }
                        
                        // 替换为新图片，使用工作目录中的文件URI
                        tierImages[index] = tierImages[index].copy(
                            uri = newUri,
                            originalUri = newUri,
                            cropPositionX = 0.5f,
                            cropPositionY = 0.5f,
                            cropScale = 1.0f,
                            isCropped = false,
                            cropRatio = 0f,
                            useCustomCrop = false,
                            customCropWidth = 0,
                            customCropHeight = 0
                        )
                        
                        AppLogger.i("替换图片: 新图片已复制到工作目录并替换，文件名: $newFileName")
                    } catch (e: Exception) {
                        AppLogger.e("替换图片失败: ${e.message}", e)
                        // 如果复制失败，回退到原来的方式（直接使用URI）
                        val uriToReturn = oldImage.originalUri ?: oldImage.uri
                        if (uriToReturn !in pendingImages) {
                            pendingImages = pendingImages + uriToReturn
                        }
                        tierImages[index] = tierImages[index].copy(
                            uri = uri,
                            originalUri = uri,
                            cropPositionX = 0.5f,
                            cropPositionY = 0.5f,
                            cropScale = 1.0f,
                            isCropped = false,
                            cropRatio = 0f,
                            useCustomCrop = false,
                            customCropWidth = 0,
                            customCropHeight = 0
                        )
                    }
                }
            }
            imageToReplace = null
        }
    }

    // 图片选择器（单选，用于选择小图标设置到具体槽位）
    val badgeImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            if (badgeSelectionTarget == 0) {
                // 添加小图标到工作目录（不设置到具体图片）- 单选情况
                scope.launch {
                    try {
                        val workBadgesDir = File(presetManager.getWorkImagesDirectory(), PresetManager.BADGES_FOLDER_NAME)
                        workBadgesDir.mkdirs()
                        // 从URI获取原始文件名，保留用户排序
                        val originalFileName = FileUtils.getFileNameFromUri(context, uri)
                        val fileName = originalFileName ?: "${System.currentTimeMillis()}.png"
                        val destFile = File(workBadgesDir, fileName)
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            FileOutputStream(destFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                        AppLogger.i("添加小图标到工作目录: ${destFile.absolutePath}")
                        // 刷新对话框以显示新添加的小图标
                        badgeDialogRefreshKey++
                        showToastWithoutIcon(context, context.getString(R.string.badge_added))
                    } catch (e: Exception) {
                        AppLogger.e("添加小图标失败: ${e.message}")
                        showToastWithoutIcon(context, context.getString(R.string.badge_add_failed, e.message), Toast.LENGTH_LONG)
                    } finally {
                        // 重置防重复点击状态
                        isBadgePickerLaunching = false
                        // 恢复草稿保存
                        onResumeDraftSave?.invoke()
                    }
                }
            } else if (imageForBadge != null) {
                // 为图片添加小图标，同时保存到工作目录
                scope.launch {
                    try {
                        // 先将小图标复制到工作目录
                        val workBadgesDir = File(presetManager.getWorkImagesDirectory(), PresetManager.BADGES_FOLDER_NAME)
                        workBadgesDir.mkdirs()
                        // 从URI获取原始文件名，保留用户排序
                        val originalFileName = FileUtils.getFileNameFromUri(context, uri)
                        val fileName = originalFileName ?: "${System.currentTimeMillis()}.png"
                        val destFile = File(workBadgesDir, fileName)
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            FileOutputStream(destFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                        // 使用工作目录中的文件URI
                        val workUri = Uri.fromFile(destFile)
                        val index = tierImages.indexOfFirst { it.id == imageForBadge!!.id }
                        if (index != -1) {
                            when (badgeSelectionTarget) {
                                1 -> tierImages[index] = tierImages[index].copy(badgeUri = workUri)
                                2 -> tierImages[index] = tierImages[index].copy(badgeUri2 = workUri)
                                3 -> tierImages[index] = tierImages[index].copy(badgeUri3 = workUri)
                            }
                            AppLogger.i("为图片添加小图标${badgeSelectionTarget}: ${imageForBadge!!.id}, 已保存到工作目录")
                            // 更新 imageForBadge 为最新的对象引用
                            imageForBadge = tierImages[index]
                        }
                        // 刷新小图标预览区域
                        badgeDialogRefreshKey++
                    } catch (e: Exception) {
                        AppLogger.e("添加小图标到工作目录失败: ${e.message}")
                        // 如果保存失败，仍然使用原始URI
                        val index = tierImages.indexOfFirst { it.id == imageForBadge!!.id }
                        if (index != -1) {
                            when (badgeSelectionTarget) {
                                1 -> tierImages[index] = tierImages[index].copy(badgeUri = uri)
                                2 -> tierImages[index] = tierImages[index].copy(badgeUri2 = uri)
                                3 -> tierImages[index] = tierImages[index].copy(badgeUri3 = uri)
                            }
                            imageForBadge = tierImages[index]
                        }
                        // 刷新小图标预览区域
                        badgeDialogRefreshKey++
                    } finally {
                        // 重置选择目标（在协程完成后执行）
                        badgeSelectionTarget = 0
                        // 重置防重复点击状态
                        isBadgePickerLaunching = false
                        // 恢复草稿保存
                        onResumeDraftSave?.invoke()
                    }
                }
            } else {
                // imageForBadge 为 null，重置状态
                badgeSelectionTarget = 0
                isBadgePickerLaunching = false
                // 恢复草稿保存
                onResumeDraftSave?.invoke()
            }
        } else {
            // uri 为 null，重置状态
            badgeSelectionTarget = 0
            isBadgePickerLaunching = false
            // 恢复草稿保存
            onResumeDraftSave?.invoke()
        }
    }

    // 图片选择器（多选，用于批量添加小图标到工作目录，最多20张）
    val badgeImagePickerMultiple = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 20)
    ) { uris ->
        if (uris.isNotEmpty()) {
            scope.launch {
                var successCount = 0
                var failCount = 0
                uris.forEach { uri ->
                    try {
                        val workBadgesDir = File(presetManager.getWorkImagesDirectory(), PresetManager.BADGES_FOLDER_NAME)
                        workBadgesDir.mkdirs()
                        // 从URI获取原始文件名，保留用户排序
                        val originalFileName = FileUtils.getFileNameFromUri(context, uri)
                        val fileName = originalFileName ?: "${System.currentTimeMillis()}_${successCount}.png"
                        val destFile = File(workBadgesDir, fileName)
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            FileOutputStream(destFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                        successCount++
                    } catch (e: Exception) {
                        failCount++
                        AppLogger.e("批量添加小图标失败: ${e.message}")
                    }
                }
                // 刷新对话框以显示新添加的小图标
                badgeDialogRefreshKey++
                // 使用统计日志格式
                AppLogger.i("批量添加小图标完成: 成功${successCount}张${if (failCount > 0) ", 失败${failCount}张" else ""}")
                if (successCount > 0) {
                    showToastWithoutIcon(context, context.getString(R.string.badges_added, successCount))
                }
                if (failCount > 0) {
                    showToastWithoutIcon(context, context.getString(R.string.badges_add_failed_partial, failCount), Toast.LENGTH_LONG)
                }
            }
        }
        // 重置防重复点击状态
        isBadgePickerLaunching = false
        // 恢复草稿保存
        onResumeDraftSave?.invoke()
    }

    // 权限申请（不自动打开图片选择器）
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // 权限申请完成后，如果是首次启动则显示语言选择对话框
        if (shouldShowLanguageOnFirstLaunch) {
            showLanguageDialog = true
            // 标记已显示过语言选择对话框
            prefs.edit().putBoolean("show_language_on_first_launch", false).apply()
        }
        // 重置所有防重复点击状态（权限申请完成后，无论成功与否）
        isBadgePickerLaunching = false
        isImagePickerLaunching = false
        AppLogger.d("权限申请完成，重置防重复点击状态: granted=$isGranted")
    }

    // 应用启动时检查权限（仅申请权限，不自动打开图片选择器）
    LaunchedEffect(Unit) {
        val permission = FileUtils.getReadStoragePermission()
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(permission)
        } else {
            // 已有权限，如果是首次启动则显示语言选择对话框
            if (shouldShowLanguageOnFirstLaunch) {
                showLanguageDialog = true
                // 标记已显示过语言选择对话框
                prefs.edit().putBoolean("show_language_on_first_launch", false).apply()
            }
        }
    }

    // 辅助函数：启动小图标选择器
    fun launchBadgePicker(target: Int) {
        if (!isBadgePickerLaunching) {
            isBadgePickerLaunching = true
            badgeSelectionTarget = target
            AppLogger.i("选择小图标$target: ${imageForBadge!!.id}")
            val permission = FileUtils.getReadStoragePermission()
            when {
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                    onSkipDraftSave?.invoke()
                    badgeImagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
                else -> {
                    permissionLauncher.launch(permission)
                }
            }
        }
    }

    // 辅助函数：删除小图标
    fun deleteBadge(target: Int) {
        val index = tierImages.indexOfFirst { it.id == imageForBadge!!.id }
        if (index != -1) {
            tierImages[index] = when (target) {
                1 -> tierImages[index].copy(badgeUri = null)
                2 -> tierImages[index].copy(badgeUri2 = null)
                3 -> tierImages[index].copy(badgeUri3 = null)
                else -> tierImages[index]
            }
            AppLogger.d("删除小图标$target - 图片ID: ${imageForBadge!!.id}")
            imageForBadge = tierImages[index]
        }
    }

    // 辅助函数：启动小图标选择器用于添加新小图标到工作目录（支持多选，最多20张）
    fun launchBadgePickerForAdding() {
        if (!isBadgePickerLaunching) {
            isBadgePickerLaunching = true
            badgeSelectionTarget = 0 // 0 表示添加到工作目录，不设置到具体槽位
            AppLogger.i("添加新小图标到工作目录（多选模式，最多20张）")
            val permission = FileUtils.getReadStoragePermission()
            when {
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                    onSkipDraftSave?.invoke()
                    badgeImagePickerMultiple.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
                else -> {
                    permissionLauncher.launch(permission)
                }
            }
        }
    }

    // 辅助函数：删除小图标文件
    fun deleteBadgeFile(badgeUri: Uri, presetManager: PresetManager): Boolean {
        return try {
            AppLogger.d("尝试删除小图标 - URI: $badgeUri, Path: ${badgeUri.path}")
            val path = badgeUri.path ?: run {
                AppLogger.w("小图标URI path为空")
                return false
            }
            val file = File(path)
            AppLogger.d("小图标文件对象 - 路径: ${file.absolutePath}, 是否存在: ${file.exists()}")
            if (file.exists()) {
                val deleted = file.delete()
                if (deleted) {
                    AppLogger.i("删除小图标文件成功: ${file.name}")
                } else {
                    AppLogger.w("删除小图标文件失败: ${file.name} (文件可能正在被使用或权限不足)")
                }
                deleted
            } else {
                AppLogger.w("小图标文件不存在: ${file.absolutePath}")
                false
            }
        } catch (e: Exception) {
            AppLogger.e("删除小图标文件异常: ${e.message}", e)
            false
        }
    }

    // 双击返回退出应用
    var backPressedTime by remember { mutableLongStateOf(0L) }
    BackHandler {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime > 2000) {
            backPressedTime = currentTime
        } else {
            // 双击退出：保存草稿并清理资源
            (context as MainActivity).exitAppWithCleanup()
        }
    }

    val extendedColors = LocalExtendedColors.current

    // 草稿恢复对话框
    if (showDraftRestoreDialog && draftConfigData != null) {
        DraftRestoreDialog(
            title = draftConfigData!!.title,
            author = draftConfigData!!.author,
            onDismiss = {
                // 取消恢复，清理草稿
                presetManager.cleanupDraft()
                showDraftRestoreDialog = false
                draftConfigData = null
                AppLogger.i("用户取消恢复草稿")
            },
            onRestore = {
                // 用户确认恢复，先关闭恢复对话框，显示加载对话框
                showDraftRestoreDialog = false
                showDraftLoadingDialog = true
                AppLogger.i("用户确认恢复草稿，开始加载...")

                scope.launch {
                    // 让出时间片，确保UI有时间显示加载对话框
                    yield()
                    try {
                        val draftFile = presetManager.obtainDraftFile()
                        if (draftFile != null) {
                            // 在后台线程执行耗时操作
                            val result = withContext(Dispatchers.IO) {
                                presetManager.restoreDraft(draftFile)
                            }
                            // 更新UI状态
                            tiers.clear()
                            tiers.addAll(result.tiers.map { tierData ->
                                TierItem(tierData.label, try {
                                    Color(android.graphics.Color.parseColor("#${tierData.color}"))
                                } catch (e: Exception) { Color.Gray })
                            })
                            tierImages.clear()
                            tierImages.addAll(result.tierImages.map { appliedImage ->
                                TierImage(
                                    id = appliedImage.id,
                                    tierLabel = appliedImage.tierLabel,
                                    uri = appliedImage.uri,
                                    name = appliedImage.name,
                                    badgeUri = appliedImage.badgeUri,
                                    badgeUri2 = appliedImage.badgeUri2,
                                    badgeUri3 = appliedImage.badgeUri3,
                                    originalUri = appliedImage.originalUri,
                                    cropPositionX = appliedImage.cropPositionX,
                                    cropPositionY = appliedImage.cropPositionY,
                                    cropScale = appliedImage.cropScale,
                                    isCropped = appliedImage.isCropped,
                                    cropRatio = appliedImage.cropRatio,
                                    useCustomCrop = appliedImage.useCustomCrop,
                                    customCropWidth = appliedImage.customCropWidth,
                                    customCropHeight = appliedImage.customCropHeight
                                )
                            })
                            pendingImages = result.pendingImages
                            tierListTitle = result.title
                            authorName = result.author
                            // 清理旧的裁剪设置并应用新的
                            prefs.edit()
                                .remove("crop_ratio")
                                .remove("custom_crop_width")
                                .remove("custom_crop_height")
                                .remove("use_custom_crop_size")
                                .putInt("custom_crop_width", result.customCropWidth)
                                .putInt("custom_crop_height", result.customCropHeight)
                                .putBoolean("use_custom_crop_size", result.useCustomCropSize)
                                .putFloat("crop_ratio", result.cropRatio)
                                .apply()
                            // 清空层级位置信息,确保使用草稿中的层级标签
                            tierRowPositions = emptyMap()
                            showDraftLoadingDialog = false
                            draftConfigData = null

                            showToastWithoutIcon(context, context.getString(R.string.draft_restored))
                            AppLogger.i("用户恢复草稿成功")
                        } else {
                            throw IllegalStateException("加载草稿失败")
                        }
                    } catch (e: Exception) {
                        AppLogger.e("恢复草稿失败", e)
                        showDraftLoadingDialog = false
                        draftConfigData = null
                        showToastWithoutIcon(context, context.getString(R.string.draft_restore_failed))
                    }
                }
            }
        )
    }

    // 草稿加载中对话框
    if (showDraftLoadingDialog) {
        LoadingDialog(message = stringResource(R.string.loading_resources))
    }

    Scaffold(
        containerColor = extendedColors.background,
        topBar = {
            // 使用自定义布局实现标题真正居中
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(extendedColors.background)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                // 左侧按钮组
                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 主题切换按钮
                    IconButton(onClick = {
                        val newTheme = !isDarkTheme
                        onThemeChange(newTheme)
                        AppLogger.i("切换主题: ${if (newTheme) "深色" else "浅色"}")
                    }) {
                        // 当前深色主题显示太阳(切换到浅色),当前浅色主题显示月亮(切换到深色)
                        val iconRes = if (isDarkTheme) R.drawable.ic_sun_light else R.drawable.ic_moon_light
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = if (isDarkTheme) stringResource(R.string.switch_to_light_theme) else stringResource(R.string.switch_to_dark_theme),
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // 居中的标题
                Text(
                    text = "-$tierListTitle-",
                    modifier = Modifier.clickable { showEditTitleDialog = true },
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge
                )

                // 右侧按钮组
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 资源管理按钮
                    IconButton(onClick = { showResourceManageDialog = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_resource_manage),
                            contentDescription = stringResource(R.string.resource_management),
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    // 功能菜单按钮
                    IconButton(onClick = { showSettingsMenu = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_menu_light),
                            contentDescription = stringResource(R.string.settings),
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        bottomBar = {
            // 底部按钮栏 - 现代化设计,自适应缩放
            val buttonHeight = (48 * scaleFactor).dp
            val buttonFontSize = (16 * scaleFactor).sp
            val horizontalPadding = (16 * scaleFactor).dp
            val verticalPadding = (12 * scaleFactor).dp
            val buttonSpacing = (12 * scaleFactor).dp
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(extendedColors.background)
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding)
                    .windowInsetsPadding(WindowInsets.navigationBars),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing, Alignment.CenterHorizontally)
            ) {
                // 保存按钮 - 使用轮廓样式
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            previewIsDarkTheme = isDarkTheme
                            val bitmap = generateTierListBitmap(context, tiers, tierImages, tierListTitle, authorName, previewIsDarkTheme, externalBadgeEnabled, disableCustomFont, nameBelowImage)
                            previewBitmap = bitmap
                            showPreviewDialog = true
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(buttonHeight),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.outline
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        text = stringResource(R.string.save),
                        fontSize = buttonFontSize,
                        fontWeight = FontWeight.Medium
                    )
                }

                // 重置按钮 - 使用轮廓样式
                OutlinedButton(
                    onClick = {
                        // 防止重复点击
                        if (isResetting) return@OutlinedButton
                        
                        // 检查是否已经是默认状态（默认模板且无图片）
                        val isDefaultState = tierImages.isEmpty() && 
                                            tiers.size == defaultTiers.size &&
                                            tiers.zip(defaultTiers).all { (current, default) ->
                                                current.label == default.label && current.color == default.color
                                            }
                        
                        if (isDefaultState) {
                            // 已经是默认状态,直接返回不执行任何操作
                            return@OutlinedButton
                        }
                        
                        isResetting = true
                        
                        // 将层级中的图片返回到待分级区域（不返回小图标，返回原图）
                        val imagesToReturn = tierImages.map { it.originalUri ?: it.uri }.filter { it !in pendingImages }
                        
                        // 重置层级为默认模板
                        tiers.clear()
                        tiers.addAll(defaultTiers)
                        tierImages.clear()
                        // 清空层级位置信息,确保使用默认模板的层级标签
                        tierRowPositions = emptyMap()
                        // 清理裁剪设置
                        prefs.edit()
                            .remove("crop_ratio")
                            .remove("custom_crop_width")
                            .remove("custom_crop_height")
                            .remove("use_custom_crop_size")
                            .apply()

                        // 将层级图片添加到待分级区域（保留原有图片）
                        if (imagesToReturn.isNotEmpty()) {
                            pendingImages = pendingImages + imagesToReturn
                            AppLogger.i("重置时将 ${imagesToReturn.size} 张图片返回到待分级区域")
                        }

                        tierListTitle = context.getString(R.string.default_title)
                        authorName = ""
                        AppLogger.i("重置梯度表完成，待分级区域 ${pendingImages.size} 张图片")
                        showToastWithoutIcon(context, context.getString(R.string.reset_success))
                        
                        // 延迟重置状态，防止快速连续点击
                        scope.launch {
                            delay(500)
                            isResetting = false
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(buttonHeight),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.outline
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        text = stringResource(R.string.reset),
                        fontSize = buttonFontSize,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(extendedColors.background)
                .padding(innerPadding)
        ) {
            // 待添加图片区域（固定显示）
            PendingImagesSection(
                images = pendingImages,
                tiers = tiers,
                tierRowPositions = tierRowPositions,
                imageSize = imageSize,
                imageCornerRadius = imageCornerRadius,
                onClear = {
                        // 清理待添加图片的资源文件（保留层级中正在使用的图片）
                        val clearedCount = pendingImages.size
                        try {
                            val tierImageUris = tierImages.map { it.uri }
                            var cleanedCount = 0
                            pendingImages.forEach { uri ->
                                // 只清理不在层级中使用的图片
                                if (uri !in tierImageUris) {
                                    val fileName = uri.lastPathSegment
                                    if (fileName != null && (fileName.startsWith("imported_") || fileName.startsWith("builtin_"))) {
                                        val file = File(context.filesDir, fileName)
                                        if (file.exists() && file.delete()) {
                                            cleanedCount++
                                        }
                                    }
                                }
                            }
                            AppLogger.i("清空待添加图片 - 清理资源文件: ${cleanedCount}个")
                        } catch (e: Exception) {
                            AppLogger.e("清空待添加图片时清理资源失败", e)
                        }
                        pendingImages = emptyList()
                        // 显示清空提示
                        if (clearedCount > 0) {
                            showToastWithoutIcon(context, context.getString(R.string.images_cleared, clearedCount))
                        }
                    },
                onAdd = {
                    // 打开图片选择器添加图片到待分级区域
                    if (!isImagePickerLaunching) {
                        isImagePickerLaunching = true
                        val permission = FileUtils.getReadStoragePermission()
                        when {
                            ContextCompat.checkSelfPermission(
                                context,
                                permission
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                onSkipDraftSave?.invoke()
                                addToPendingPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                            else -> {
                                permissionLauncher.launch(permission)
                            }
                        }
                    }
                },
                onDragStart = { uri ->
                    isDraggingPendingImage = true
                    draggedPendingImageUri = uri
                },
                onDragEnd = {
                    isDraggingPendingImage = false
                    draggedPendingImageUri = null
                },
                onDropOnTier = { uri, tierLabel ->
                    tierImages.add(TierImage(UUID.randomUUID().toString(), tierLabel, uri))
                    pendingImages = pendingImages.filter { it != uri }
                    AppLogger.i("拖动添加图片到层级: $tierLabel")
                },
                onDeleteImage = { uri ->
                    // 从待选区删除图片
                    pendingImages = pendingImages.filter { it != uri }
                    // 清理资源文件
                    try {
                        val fileName = uri.lastPathSegment
                        if (fileName != null && (fileName.startsWith("imported_") || fileName.startsWith("builtin_"))) {
                            val file = File(context.filesDir, fileName)
                            if (file.exists() && file.delete()) {
                                AppLogger.d("删除待选区图片并清理资源文件: $fileName")
                            }
                        }
                    } catch (e: Exception) {
                        AppLogger.e("删除待选区图片时清理资源失败", e)
                    }
                },
                floatOffsetX = floatOffsetX,
                floatOffsetY = floatOffsetY,
                onPositionUpdate = { rect ->
                    pendingSectionRect = rect
                }
            )
            
            // 梯度表主体
            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                itemsIndexed(tiers, key = { _, tier -> tier.label }) { index, tier ->
                    val tierImageList = tierImages.filter { it.tierLabel == tier.label }
                    SwipeableTierRow(
                        tier = tier,
                        images = tierImageList,
                        pendingImages = if (isDraggingPendingImage) emptyList() else pendingImages,
                        tierLabelWidth = tierLabelWidth,
                        imageSize = imageSize,
                        tierRowHeight = tierRowHeight,
                        labelFontSize = labelFontSize,
                        tierLabelCornerRadius = tierLabelCornerRadius,
                        imageCornerRadius = imageCornerRadius,
                        onTierClick = {
                            editingTier = tier
                            showEditNameDialog = true
                            AppLogger.i("点击层级编辑名称: ${tier.label}")
                        },
                        onTierLongClick = {
                            // 长按功能未实现，仅作为占位回调
                        },
                        onTierDoubleClick = {
                            editingTier = tier
                            showColorPickerDialog = true
                            AppLogger.i("双击层级编辑颜色: ${tier.label}")
                        },
                        onAddImage = { uri ->
                            // 正常点击模式
                            tierImages.add(TierImage(UUID.randomUUID().toString(), tier.label, uri))
                            pendingImages = pendingImages.filter { it != uri }
                            AppLogger.i("添加图片到层级: ${tier.label}")
                        },
                        onPositionUpdate = { tierLabel, rect ->
                            tierRowPositions = tierRowPositions + (tierLabel to rect)
                        },
                        selectedImageForDrag = selectedImageForDrag,
                        onImageClick = { image, imgIndex ->
                            if (selectedImageForDrag == null) {
                                // 没有选中图片，单击打开操作对话框
                                selectedImageForAction = image
                                showImageActionDialog = true
                                AppLogger.i("单击图片打开操作对话框: ${image.tierLabel}")
                            } else if (selectedImageForDrag!!.id == image.id) {
                                // 单击已选中的图片，取消选中
                                selectedImageForDrag = null
                                AppLogger.i("取消选中图片: ${image.tierLabel}")
                            } else {
                                // 已选中图片，单击其他图片交换位置
                                val fromIndex = tierImages.indexOfFirst { it.id == selectedImageForDrag!!.id }
                                val toIndex = tierImages.indexOfFirst { it.id == image.id }
                                if (fromIndex != -1 && toIndex != -1 && fromIndex != toIndex) {
                                    val fromImage = tierImages[fromIndex]
                                    val toImage = tierImages[toIndex]

                                    // 无论是否同一层级，都交换图片的uri（内容）、name（命名）、badgeUri（小图标）和originalUri（原图）
                                    // 保持各自的id和tierLabel不变，这样图片在各自层级中的位置不变
                                    val newFromImage = fromImage.copy(
                                        uri = toImage.uri,
                                        name = toImage.name,
                                        badgeUri = toImage.badgeUri,
                                        badgeUri2 = toImage.badgeUri2,
                                        badgeUri3 = toImage.badgeUri3,
                                        originalUri = toImage.originalUri,
                                        cropPositionX = toImage.cropPositionX,
                                        cropPositionY = toImage.cropPositionY,
                                        cropScale = toImage.cropScale,
                                        isCropped = toImage.isCropped,
                                        cropRatio = toImage.cropRatio,
                                        useCustomCrop = toImage.useCustomCrop,
                                        customCropWidth = toImage.customCropWidth,
                                        customCropHeight = toImage.customCropHeight
                                    )
                                    val newToImage = toImage.copy(
                                        uri = fromImage.uri,
                                        name = fromImage.name,
                                        badgeUri = fromImage.badgeUri,
                                        badgeUri2 = fromImage.badgeUri2,
                                        badgeUri3 = fromImage.badgeUri3,
                                        originalUri = fromImage.originalUri,
                                        cropPositionX = fromImage.cropPositionX,
                                        cropPositionY = fromImage.cropPositionY,
                                        cropScale = fromImage.cropScale,
                                        isCropped = fromImage.isCropped,
                                        cropRatio = fromImage.cropRatio,
                                        useCustomCrop = fromImage.useCustomCrop,
                                        customCropWidth = fromImage.customCropWidth,
                                        customCropHeight = fromImage.customCropHeight
                                    )
                                    tierImages[fromIndex] = newFromImage
                                    tierImages[toIndex] = newToImage

                                    // 更新相关引用，确保裁切、查看、替换等操作使用正确的图片数据
                                    if (selectedImageForAction?.id == fromImage.id) {
                                        selectedImageForAction = newFromImage
                                    } else if (selectedImageForAction?.id == toImage.id) {
                                        selectedImageForAction = newToImage
                                    }
                                    if (imageToReplace?.id == fromImage.id) {
                                        imageToReplace = newFromImage
                                    } else if (imageToReplace?.id == toImage.id) {
                                        imageToReplace = newToImage
                                    }
                                    if (imageForBadge?.id == fromImage.id) {
                                        imageForBadge = newFromImage
                                    } else if (imageForBadge?.id == toImage.id) {
                                        imageForBadge = newToImage
                                    }

                                    AppLogger.i("交换图片位置: ${fromImage.tierLabel} <-> ${toImage.tierLabel}")
                                }
                                // 交换后取消选中状态
                                selectedImageForDrag = null
                            }
                        },
                        onImageLongClick = { image, imgIndex ->
                            // 长按功能未实现，仅作为占位回调
                        },
                        onImageDoubleClick = { image, imgIndex ->
                            if (selectedImageForDrag == null) {
                                // 没有选中图片，双击选中图片
                                selectedImageForDrag = image
                                AppLogger.i("双击选中图片: ${image.tierLabel}")
                            } else if (selectedImageForDrag!!.id == image.id) {
                                // 双击已选中的图片，取消选中
                                selectedImageForDrag = null
                                AppLogger.i("双击取消选中图片: ${image.tierLabel}")
                            } else {
                                // 已选中图片，双击其他图片交换位置并取消选中
                                val fromIndex = tierImages.indexOfFirst { it.id == selectedImageForDrag!!.id }
                                val toIndex = tierImages.indexOfFirst { it.id == image.id }
                                if (fromIndex != -1 && toIndex != -1 && fromIndex != toIndex) {
                                    val fromImage = tierImages[fromIndex]
                                    val toImage = tierImages[toIndex]

                                    // 无论是否同一层级，都交换图片的uri（内容）、name（命名）、badgeUri（小图标）和originalUri（原图）
                                    // 保持各自的id和tierLabel不变，这样图片在各自层级中的位置不变
                                    val newFromImage = fromImage.copy(
                                        uri = toImage.uri,
                                        name = toImage.name,
                                        badgeUri = toImage.badgeUri,
                                        badgeUri2 = toImage.badgeUri2,
                                        badgeUri3 = toImage.badgeUri3,
                                        originalUri = toImage.originalUri,
                                        cropPositionX = toImage.cropPositionX,
                                        cropPositionY = toImage.cropPositionY,
                                        cropScale = toImage.cropScale,
                                        isCropped = toImage.isCropped,
                                        cropRatio = toImage.cropRatio,
                                        useCustomCrop = toImage.useCustomCrop,
                                        customCropWidth = toImage.customCropWidth,
                                        customCropHeight = toImage.customCropHeight
                                    )
                                    val newToImage = toImage.copy(
                                        uri = fromImage.uri,
                                        name = fromImage.name,
                                        badgeUri = fromImage.badgeUri,
                                        badgeUri2 = fromImage.badgeUri2,
                                        badgeUri3 = fromImage.badgeUri3,
                                        originalUri = fromImage.originalUri,
                                        cropPositionX = fromImage.cropPositionX,
                                        cropPositionY = fromImage.cropPositionY,
                                        cropScale = fromImage.cropScale,
                                        isCropped = fromImage.isCropped,
                                        cropRatio = fromImage.cropRatio,
                                        useCustomCrop = fromImage.useCustomCrop,
                                        customCropWidth = fromImage.customCropWidth,
                                        customCropHeight = fromImage.customCropHeight
                                    )
                                    tierImages[fromIndex] = newFromImage
                                    tierImages[toIndex] = newToImage

                                    // 更新相关引用，确保裁切、查看、替换等操作使用正确的图片数据
                                    if (selectedImageForAction?.id == fromImage.id) {
                                        selectedImageForAction = newFromImage
                                    } else if (selectedImageForAction?.id == toImage.id) {
                                        selectedImageForAction = newToImage
                                    }
                                    if (imageToReplace?.id == fromImage.id) {
                                        imageToReplace = newFromImage
                                    } else if (imageToReplace?.id == toImage.id) {
                                        imageToReplace = newToImage
                                    }
                                    if (imageForBadge?.id == fromImage.id) {
                                        imageForBadge = newFromImage
                                    } else if (imageForBadge?.id == toImage.id) {
                                        imageForBadge = newToImage
                                    }

                                    AppLogger.i("双击交换图片位置: ${fromImage.tierLabel} <-> ${toImage.tierLabel}")
                                }
                                selectedImageForDrag = null
                            }
                        },
                        onDeleteTier = {
                            // 使用该层级的标签查找当前索引（避免交换图片后索引过期）
                            val currentIndex = tiers.indexOfFirst { it.label == tier.label }
                            if (currentIndex != -1) {
                                // 将该层级的所有图片返回到待放置区（避免重复，返回原图）
                                val imagesToReturn = tierImages.filter { it.tierLabel == tier.label }
                                val newPendingImages = imagesToReturn.map { it.originalUri ?: it.uri }
                                    .filter { it !in pendingImages }
                                if (newPendingImages.isNotEmpty()) {
                                    pendingImages = pendingImages + newPendingImages
                                }
                                val returnedCount = newPendingImages.size
                                val deletedCount = imagesToReturn.size
                                tiers.removeAt(currentIndex)
                                tierImages.removeAll { it.tierLabel == tier.label }
                                // 从 tierRowPositions 中移除该层级的位置信息
                                tierRowPositions = tierRowPositions - tier.label
                                AppLogger.i("删除层级 - 层级: ${tier.label}, 删除图片: ${deletedCount}张, 返回待放置区: ${returnedCount}张")
                            } else {
                                AppLogger.w("删除层级失败，找不到层级: ${tier.label}")
                            }
                        },
                        onPickImage = {
                            // 防止重复打开图片选择器
                            if (!isImagePickerLaunching) {
                                isImagePickerLaunching = true
                                
                                // 点击右侧区域打开图片选择器
                                val permission = FileUtils.getReadStoragePermission()
                                when {
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        permission
                                    ) == PackageManager.PERMISSION_GRANTED -> {
                                        onSkipDraftSave?.invoke()
                                        imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                    }
                                    else -> {
                                        permissionLauncher.launch(permission)
                                    }
                                }
                            }
                        },
                        disableClickAdd = disableClickAdd,
                        isDraggingPendingImage = isDraggingPendingImage,
                        onMoveSelectedImageToTier = {
                            // 移动选中的图片到当前层级
                            selectedImageForDrag?.let { selectedImage ->
                                val index = tierImages.indexOfFirst { it.id == selectedImage.id }
                                if (index != -1) {
                                    val oldTier = tierImages[index].tierLabel
                                    // 从原位置移除图片，并修改层级标签
                                    val movedImage = tierImages.removeAt(index).copy(tierLabel = tier.label)
                                    // 将图片添加到新层级的末尾（保持该层级原有图片顺序）
                                    tierImages.add(movedImage)
                                    AppLogger.i("移动图片: $oldTier -> ${tier.label} (添加到层级末尾)")
                                }
                                selectedImageForDrag = null
                            }
                        },
                        // 层级图片拖拽相关参数
                        tierRowPositions = tierRowPositions,
                        pendingSectionRect = pendingSectionRect,
                        draggingTierImage = draggingTierImage,
                        onTierImageDragStart = { image, center ->
                            // 从 tierImages 列表中查找最新的图片对象,确保获取正确的 URI
                            val latestImage = tierImages.find { it.id == image.id } ?: image
                            draggingTierImage = latestImage
                            draggingTierImagePosition = center
                            AppLogger.i("开始拖拽层级图片: ${latestImage.tierLabel}, URI: ${latestImage.uri}")
                        },
                        onTierImageDrag = { center, targetTier, deleteMode, toPending ->
                            draggingTierImagePosition = center
                            draggingTierImageTarget = targetTier
                            isDraggingTierImageDeleteMode = deleteMode
                            isDraggingTierImageToPending = toPending
                        },
                        onTierImageDragEnd = { image, targetTier, deleteMode, toPending ->
                            if (deleteMode) {
                                // 向上拖拽超过待分级区 - 删除图片
                                val index = tierImages.indexOfFirst { it.id == image.id }
                                if (index != -1) {
                                    tierImages.removeAt(index)
                                    AppLogger.d("拖拽删除层级图片 - 层级: ${image.tierLabel}")
                                }
                            } else if (toPending) {
                                // 拖拽至待分级区
                                val index = tierImages.indexOfFirst { it.id == image.id }
                                if (index != -1) {
                                    // 返回原图到待分级区域
                                    val imageToMove = tierImages[index]
                                    val uriToReturn = imageToMove.originalUri ?: imageToMove.uri
                                    tierImages.removeAt(index)
                                    if (uriToReturn !in pendingImages) {
                                        pendingImages = pendingImages + uriToReturn
                                    }
                                    AppLogger.d("移动图片到待分级区 - 原层级: ${image.tierLabel}")
                                }
                            } else if (targetTier != null && targetTier != image.tierLabel) {
                                // 跨层级移动
                                val index = tierImages.indexOfFirst { it.id == image.id }
                                if (index != -1) {
                                    val oldTier = tierImages[index].tierLabel
                                    val movedImage = tierImages.removeAt(index).copy(tierLabel = targetTier)
                                    tierImages.add(movedImage)
                                    AppLogger.d("跨层级移动图片: $oldTier -> $targetTier")
                                }
                            }
                            draggingTierImage = null
                            draggingTierImageTarget = null
                            isDraggingTierImageDeleteMode = false
                            isDraggingTierImageToPending = false
                        },
                        // 新增：层级顺序调整参数
                        onTierReorder = { fromIndex, toIndex ->
                            if (fromIndex != toIndex && fromIndex in tiers.indices && toIndex in tiers.indices) {
                                val tier = tiers.removeAt(fromIndex)
                                tiers.add(toIndex, tier)
                                AppLogger.i("调整层级顺序: $fromIndex -> $toIndex")
                            }
                        },
                        tierIndex = index,
                        totalTiers = tiers.size,
                        allTierLabels = tiers.map { it.label },
                        // 列表状态用于自动滚动
                        listState = listState
                    )
                }

                // 添加新层级按钮
                item {
                    AddTierButton(
                        onClick = {
                            val newLabel = ColorUtils.generateNextLabel(tiers.map { it.label })
                            tiers.add(TierItem(newLabel, ColorUtils.generateRandomColor()))
                            AppLogger.i("添加新层级: $newLabel")
                        }
                    )
                }

                // 作者信息输入
                item {
                    AuthorInfoSection(
                        authorName = authorName,
                        onClick = { showEditAuthorDialog = true }
                    )
                }
            }
        }
        
        // 浮动显示的层级图片拖拽
        if (draggingTierImage != null) {
            FloatingDragImage(
                uri = draggingTierImage!!.uri,
                position = draggingTierImagePosition,
                dropTarget = draggingTierImageTarget,
                floatOffsetX = floatOffsetX,
                floatOffsetY = floatOffsetY,
                isDeleteMode = isDraggingTierImageDeleteMode
            )
        }
        
        // 编辑名称对话框
        if (showEditNameDialog && editingTier != null) {
            EditTierNameDialog(
                currentName = editingTier!!.label,
                existingNames = tiers.map { it.label }.filter { it != editingTier!!.label },
                onDismiss = { showEditNameDialog = false },
                onConfirm = { newName ->
                    val index = tiers.indexOfFirst { it.label == editingTier!!.label }
                    if (index != -1) {
                        val oldLabel = tiers[index].label
                        tiers[index] = tiers[index].copy(label = newName)
                        // 更新图片的层级标签
                        tierImages.forEachIndexed { i, img ->
                            if (img.tierLabel == oldLabel) {
                                tierImages[i] = img.copy(tierLabel = newName)
                            }
                        }
                        // 更新 tierRowPositions 中的 key
                        tierRowPositions[oldLabel]?.let { rect ->
                            tierRowPositions = tierRowPositions - oldLabel + (newName to rect)
                        }
                        AppLogger.i("修改层级名称: $oldLabel -> $newName")
                    }
                    showEditNameDialog = false
                }
            )
        }

        // 颜色选择器对话框
        if (showColorPickerDialog && editingTier != null) {
            // 从 tiers 列表中获取最新的颜色值
            val currentTierColor = tiers.find { it.label == editingTier!!.label }?.color ?: editingTier!!.color
            ColorPickerDialog(
                currentColor = currentTierColor,
                onDismiss = { showColorPickerDialog = false },
                onConfirm = { newColor ->
                    val index = tiers.indexOfFirst { it.label == editingTier!!.label }
                    if (index != -1) {
                        tiers[index] = tiers[index].copy(color = newColor)
                        AppLogger.i("修改层级颜色: ${editingTier!!.label}")
                    }
                    showColorPickerDialog = false
                }
            )
        }

        // 图片操作对话框
        if (showImageActionDialog && selectedImageForAction != null) {
            ImageActionDialog(
                onDismiss = { showImageActionDialog = false },
                onSetBadge = {
                    // 打开设置小图标对话框
                    imageForBadge = selectedImageForAction
                    showImageActionDialog = false
                    showSetBadgeDialog = true
                    AppLogger.i("打开设置小图标对话框: ${selectedImageForAction?.tierLabel}")
                },
                onReplace = {
                    // 重新选择图片替换
                    imageToReplace = selectedImageForAction
                    showImageActionDialog = false
                    selectedImageForAction = null
                    // 启动单选图片选择器
                    val permission = FileUtils.getReadStoragePermission()
                    when {
                        ContextCompat.checkSelfPermission(
                            context,
                            permission
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            onSkipDraftSave?.invoke()
                            replaceImagePicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        else -> {
                            permissionLauncher.launch(permission)
                        }
                    }
                },
                onMove = {
                    // 打开移动到其他层级的对话框
                    showImageActionDialog = false
                    showMoveImageDialog = true
                },
                onRename = {
                    // 打开命名编辑对话框
                    showImageActionDialog = false
                    showEditImageNameDialog = true
                },
                onView = {
                    // 打开图片查看对话框
                    showImageActionDialog = false
                    showImageViewDialog = true
                },
                onCrop = {
                    // 打开图片裁剪对话框
                    showImageActionDialog = false
                    showCropDialog = true
                }
            )
        }

        // 图片查看对话框
        if (showImageViewDialog && selectedImageForAction != null) {
            ImageViewDialog(
                imageUri = selectedImageForAction!!.uri,
                onDismiss = {
                    showImageViewDialog = false
                    selectedImageForAction = null
                },
            )
        }

        // 图片裁剪对话框
        if (showCropDialog && selectedImageForAction != null) {
            // 获取当前图片信息，用于获取原图URI和裁剪状态
            val currentImageIndex = tierImages.indexOfFirst { it.id == selectedImageForAction!!.id }
            val currentImageForCrop = if (currentImageIndex != -1) tierImages[currentImageIndex] else null
            
            // 使用原图进行裁剪（如果有原图），否则使用当前图片
            val imageUriToCrop = currentImageForCrop?.originalUri ?: selectedImageForAction!!.uri
            
            // 获取初始裁剪状态
            val initialCropState = if (currentImageForCrop != null) {
                CropState(
                    positionX = currentImageForCrop.cropPositionX,
                    positionY = currentImageForCrop.cropPositionY,
                    scale = currentImageForCrop.cropScale,
                    cropRatio = currentImageForCrop.cropRatio,
                    useCustomCrop = currentImageForCrop.useCustomCrop,
                    customCropWidth = currentImageForCrop.customCropWidth,
                    customCropHeight = currentImageForCrop.customCropHeight
                )
            } else {
                CropState()
            }
            
            ImageCropDialog(
                imageUri = imageUriToCrop,
                initialCropState = initialCropState,
                onDismiss = {
                    showCropDialog = false
                    selectedImageForAction = null
                },
                onCrop = { croppedUri, cropState ->
                    // 获取当前图片信息
                    val index = tierImages.indexOfFirst { it.id == selectedImageForAction!!.id }
                    if (index != -1) {
                        val currentImage = tierImages[index]
                        // 如果之前没有原图URI，则使用当前URI作为原图
                        // 如果已经有原图URI，则保留原来的原图URI
                        val originalImageUri = currentImage.originalUri ?: currentImage.uri
                        
                        tierImages[index] = currentImage.copy(
                            uri = croppedUri,
                            originalUri = originalImageUri,
                            cropPositionX = cropState.positionX,
                            cropPositionY = cropState.positionY,
                            cropScale = cropState.scale,
                            isCropped = true,
                            cropRatio = cropState.cropRatio,
                            useCustomCrop = cropState.useCustomCrop,
                            customCropWidth = cropState.customCropWidth,
                            customCropHeight = cropState.customCropHeight
                        )
                        AppLogger.i("图片裁剪完成: ${selectedImageForAction!!.id}, 比例: ${cropState.cropRatio}")
                        
                        // 清理旧的裁剪图片文件（如果有）
                        try {
                            val oldUri = currentImage.uri
                            if (oldUri != originalImageUri) {
                                val fileName = oldUri.lastPathSegment
                                if (fileName != null && fileName.startsWith("cropped_")) {
                                    val file = File(context.filesDir, fileName)
                                    if (file.exists() && file.delete()) {
                                        AppLogger.i("清理旧裁剪图片: $fileName")
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            AppLogger.e("清理旧裁剪图片失败", e)
                        }
                    }
                    showCropDialog = false
                    selectedImageForAction = null
                },
                presetManager = presetManager,
                onApplyToAll = { currentCropState ->

                    // 应用到所有未裁切的图片
                    var appliedCount = 0
                    tierImages.forEachIndexed { index, tierImage ->
                        if (!tierImage.isCropped) {
                            try {
                                // 加载原图
                                val originalUri = tierImage.originalUri ?: tierImage.uri
                                context.contentResolver.openInputStream(originalUri)?.use { stream ->
                                    val options = BitmapFactory.Options().apply {
                                        inJustDecodeBounds = true
                                    }
                                    BitmapFactory.decodeStream(stream, null, options)
                                    val imageWidth = options.outWidth
                                    val imageHeight = options.outHeight

                                    // 计算裁切区域
                                    val cropWidth: Int
                                    val cropHeight: Int
                                    if (currentCropState.useCustomCrop && currentCropState.customCropWidth > 0 && currentCropState.customCropHeight > 0) {
                                        cropWidth = currentCropState.customCropWidth.coerceIn(1, imageWidth)
                                        cropHeight = currentCropState.customCropHeight.coerceIn(1, imageHeight)
                                    } else {
                                        val aspectRatio = currentCropState.cropRatio
                                        if (imageWidth.toFloat() / imageHeight > aspectRatio) {
                                            cropHeight = imageHeight
                                            cropWidth = (imageHeight * aspectRatio).toInt()
                                        } else {
                                            cropWidth = imageWidth
                                            cropHeight = (imageWidth / aspectRatio).toInt()
                                        }
                                    }

                                    // 根据位置计算偏移
                                    val maxXOffset = imageWidth - cropWidth
                                    val maxYOffset = imageHeight - cropHeight
                                    val xOffset = (maxXOffset * currentCropState.positionX).toInt()
                                    val yOffset = (maxYOffset * currentCropState.positionY).toInt()

                                    // 重新加载图片进行裁切
                                    context.contentResolver.openInputStream(originalUri)?.use { cropStream ->
                                        val originalBitmap = BitmapFactory.decodeStream(cropStream)
                                        if (originalBitmap != null) {
                                            val croppedBitmap = Bitmap.createBitmap(
                                                originalBitmap,
                                                xOffset,
                                                yOffset,
                                                cropWidth,
                                                cropHeight
                                            )

                                            // 保存到工作目录
                                            val workDir = File(presetManager.getWorkImagesDirectory(), PresetManager.IMAGES_FOLDER_NAME)
                                            workDir.mkdirs()
                                            val targetFile = File(workDir, "cropped_${System.currentTimeMillis()}_${index}.webp")
                                            FileOutputStream(targetFile).use { out ->
                                                croppedBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 100, out)
                                            }

                                            // 更新图片状态
                                            tierImages[index] = tierImage.copy(
                                                uri = Uri.fromFile(targetFile),
                                                originalUri = originalUri,
                                                cropPositionX = currentCropState.positionX,
                                                cropPositionY = currentCropState.positionY,
                                                cropScale = currentCropState.scale,
                                                isCropped = true,
                                                cropRatio = currentCropState.cropRatio,
                                                useCustomCrop = currentCropState.useCustomCrop,
                                                customCropWidth = currentCropState.customCropWidth,
                                                customCropHeight = currentCropState.customCropHeight
                                            )
                                            appliedCount++

                                            // 清理
                                            if (originalBitmap != croppedBitmap) {
                                                originalBitmap.recycle()
                                            }
                                            croppedBitmap.recycle()
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                AppLogger.e("复用裁切设置到图片失败: ${tierImage.id}", e)
                            }
                        }
                    }

                    AppLogger.i("复用裁切设置完成，共应用到 $appliedCount 张图片")
                    showToastWithoutIcon(
                        context,
                        context.getString(R.string.apply_to_all_success, appliedCount),
                        Toast.LENGTH_SHORT
                    )
                }
            )
        }

        // 设置小图标对话框
        if (showSetBadgeDialog && imageForBadge != null) {
            // 使用 derivedStateOf 确保当 tierImages 变化时自动获取最新的图片对象
            val currentImage by remember(imageForBadge!!.id) {
                derivedStateOf {
                    val currentImageIndex = tierImages.indexOfFirst { it.id == imageForBadge!!.id }
                    (if (currentImageIndex != -1) tierImages[currentImageIndex] else imageForBadge)!!
                }
            }
            SetBadgeDialog(
                badgeUri1 = currentImage.badgeUri,
                badgeUri2 = currentImage.badgeUri2,
                badgeUri3 = currentImage.badgeUri3,
                presetManager = presetManager,
                onDismiss = {
                    showSetBadgeDialog = false
                    imageForBadge = null
                    badgeSelectionTarget = 0
                },
                onSelectBadge1 = { launchBadgePicker(1) },
                onSelectBadge2 = { launchBadgePicker(2) },
                onSelectBadge3 = { launchBadgePicker(3) },
                onDeleteBadge1 = { deleteBadge(1) },
                onDeleteBadge2 = { deleteBadge(2) },
                onDeleteBadge3 = { deleteBadge(3) },
                onAddBadge = { launchBadgePickerForAdding() },
                onSelectBadgeFromPreview = { badgeUri, slot ->
                    // 从预览区域选择小图标设置到指定槽位
                    val index = tierImages.indexOfFirst { it.id == imageForBadge!!.id }
                    if (index != -1) {
                        tierImages[index] = when (slot) {
                            1 -> tierImages[index].copy(badgeUri = badgeUri)
                            2 -> tierImages[index].copy(badgeUri2 = badgeUri)
                            else -> tierImages[index].copy(badgeUri3 = badgeUri)
                        }
                        AppLogger.i("从预览区域设置小图标$slot: ${imageForBadge!!.id}")
                    }
                },
                onDeleteBadgeFromPreview = { badgeUri ->
                    // 检查小图标是否被任何图片引用
                    val isInUse = tierImages.any { image ->
                        image.badgeUri == badgeUri ||
                        image.badgeUri2 == badgeUri ||
                        image.badgeUri3 == badgeUri
                    }
                    if (isInUse) {
                        // 小图标正在被使用,显示提示
                        showToastWithoutIcon(
                            context,
                            context.getString(R.string.badge_in_use_message),
                            Toast.LENGTH_LONG
                        )
                        false
                    } else {
                        // 从预览区域删除小图标
                        val success = deleteBadgeFile(badgeUri, presetManager)
                        if (success) {
                            showToastWithoutIcon(context, context.getString(R.string.badge_deleted))
                        } else {
                            showToastWithoutIcon(context, context.getString(R.string.badge_delete_failed))
                        }
                        success
                    }
                },
                externalRefreshKey = badgeDialogRefreshKey // 传入外部刷新键，添加小图标后刷新列表
            )
        }

        // 移动图片到其他层级对话框
        if (showMoveImageDialog && selectedImageForAction != null) {
            MoveImageDialog(
                tiers = tiers,
                currentTierLabel = selectedImageForAction!!.tierLabel,
                onDismiss = {
                    showMoveImageDialog = false
                    selectedImageForAction = null
                },
                onMoveToTier = { targetTierLabel ->
                    val index = tierImages.indexOfFirst { it.id == selectedImageForAction!!.id }
                    if (index != -1) {
                        val oldTier = tierImages[index].tierLabel
                        tierImages[index] = tierImages[index].copy(tierLabel = targetTierLabel)
                        AppLogger.i("移动图片: $oldTier -> $targetTierLabel")
                    }
                    showMoveImageDialog = false
                    selectedImageForAction = null
                },
                onMoveToFirst = {
                    // 将图片移到当前层级的第一位
                    val currentIndex = tierImages.indexOfFirst { it.id == selectedImageForAction!!.id }
                    if (currentIndex != -1) {
                        val currentTier = selectedImageForAction!!.tierLabel
                        // 获取当前层级的所有图片
                        val tierImagesList = tierImages.filter { it.tierLabel == currentTier }
                        if (tierImagesList.size > 1) {
                            // 找到当前层级第一张图片的索引
                            val firstIndex = tierImages.indexOfFirst { it.tierLabel == currentTier }
                            if (currentIndex != firstIndex) {
                                // 交换位置
                                val image = tierImages.removeAt(currentIndex)
                                tierImages.add(firstIndex, image)
                                AppLogger.i("移动图片到第一位: ${selectedImageForAction!!.id}")
                            }
                        }
                    }
                    showMoveImageDialog = false
                    selectedImageForAction = null
                },
                onMoveToLast = {
                    // 将图片移到当前层级的最后一位
                    val currentIndex = tierImages.indexOfFirst { it.id == selectedImageForAction!!.id }
                    if (currentIndex != -1) {
                        val currentTier = selectedImageForAction!!.tierLabel
                        // 获取当前层级的所有图片
                        val tierImagesList = tierImages.filter { it.tierLabel == currentTier }
                        if (tierImagesList.size > 1) {
                            // 找到当前层级最后一张图片的索引
                            val lastIndex = tierImages.indexOfLast { it.tierLabel == currentTier }
                            if (currentIndex != lastIndex) {
                                // 先移除,再添加到后面
                                val image = tierImages.removeAt(currentIndex)
                                // 重新计算最后位置(因为移除后索引变了)
                                val newLastIndex = tierImages.indexOfLast { it.tierLabel == currentTier }
                                tierImages.add(newLastIndex + 1, image)
                                AppLogger.i("移动图片到最后一位: ${selectedImageForAction!!.id}")
                            }
                        }
                    }
                    showMoveImageDialog = false
                    selectedImageForAction = null
                },
                onMoveLeft = {
                    // 将图片向左移动一位（与左边图片交换位置）
                    val currentIndex = tierImages.indexOfFirst { it.id == selectedImageForAction!!.id }
                    if (currentIndex != -1) {
                        val currentTier = selectedImageForAction!!.tierLabel
                        // 获取当前层级的所有图片索引
                        val tierIndices = tierImages.withIndex()
                            .filter { it.value.tierLabel == currentTier }
                            .map { it.index }
                        val currentPosition = tierIndices.indexOf(currentIndex)
                        // 如果不是第一个，则与左边图片交换
                        if (currentPosition > 0) {
                            val leftIndex = tierIndices[currentPosition - 1]
                            // 交换两个图片的位置
                            val temp = tierImages[currentIndex]
                            tierImages[currentIndex] = tierImages[leftIndex]
                            tierImages[leftIndex] = temp
                            AppLogger.i("移动图片向左: ${selectedImageForAction!!.id}")
                        }
                    }
                    showMoveImageDialog = false
                    selectedImageForAction = null
                },
                onMoveRight = {
                    // 将图片向右移动一位（与右边图片交换位置）
                    val currentIndex = tierImages.indexOfFirst { it.id == selectedImageForAction!!.id }
                    if (currentIndex != -1) {
                        val currentTier = selectedImageForAction!!.tierLabel
                        // 获取当前层级的所有图片索引
                        val tierIndices = tierImages.withIndex()
                            .filter { it.value.tierLabel == currentTier }
                            .map { it.index }
                        val currentPosition = tierIndices.indexOf(currentIndex)
                        // 如果不是最后一个，则与右边图片交换
                        if (currentPosition < tierIndices.size - 1) {
                            val rightIndex = tierIndices[currentPosition + 1]
                            // 交换两个图片的位置
                            val temp = tierImages[currentIndex]
                            tierImages[currentIndex] = tierImages[rightIndex]
                            tierImages[rightIndex] = temp
                            AppLogger.i("移动图片向右: ${selectedImageForAction!!.id}")
                        }
                    }
                    showMoveImageDialog = false
                    selectedImageForAction = null
                }
            )
        }

        // 编辑图片命名对话框
        if (showEditImageNameDialog && selectedImageForAction != null) {
            EditImageNameDialog(
                currentName = selectedImageForAction!!.name,
                onDismiss = {
                    showEditImageNameDialog = false
                    selectedImageForAction = null
                },
                onConfirm = { newName ->
                    val index = tierImages.indexOfFirst { it.id == selectedImageForAction!!.id }
                    if (index != -1) {
                        tierImages[index] = tierImages[index].copy(name = newName)
                        AppLogger.i("修改图片命名: ${selectedImageForAction!!.id} -> $newName")
                    }
                    showEditImageNameDialog = false
                    selectedImageForAction = null
                }
            )
        }

        // 编辑标题对话框
        if (showEditTitleDialog) {
            EditTitleDialog(
                currentTitle = tierListTitle,
                onDismiss = { showEditTitleDialog = false },
                onConfirm = { newTitle ->
                    tierListTitle = newTitle
                    AppLogger.i("修改标题: $newTitle")
                    showEditTitleDialog = false
                }
            )
        }

        // 编辑作者对话框
        if (showEditAuthorDialog) {
            EditAuthorDialog(
                currentAuthor = authorName,
                onDismiss = { showEditAuthorDialog = false },
                onConfirm = { newAuthor ->
                    authorName = newAuthor
                    AppLogger.i("修改作者: $newAuthor")
                    showEditAuthorDialog = false
                }
            )
        }

        // 设置菜单对话框（功能菜单）
        if (showSettingsMenu) {
            SettingsMenuDialog(
                onDismiss = { showSettingsMenu = false },
                onShowInstructions = {
                    showSettingsMenu = false
                    showInstructionsDialog = true
                },
                onShowFeedback = {
                    showSettingsMenu = false
                    showAboutDialog = true
                },
                onImagePackage = {
                    showSettingsMenu = false
                    showManagePackagesDialog = true
                },
                onShowProgramSettings = {
                    showSettingsMenu = false
                    showProgramSettingsDialog = true
                },
                onManagePresets = {
                    showSettingsMenu = false
                    showManagePresetsDialog = true
                }
            )
        }

        // 程序设置对话框
        if (showProgramSettingsDialog) {
            ProgramSettingsDialog(
                onDismiss = { showProgramSettingsDialog = false },
                disableClickAdd = disableClickAdd,
                onToggleDisableClickAdd = { newValue ->
                    disableClickAdd = newValue
                    prefs.edit().putBoolean("disable_click_add", newValue).apply()
                    AppLogger.i("设置 禁用加添: $newValue")
                },
                floatOffsetX = floatOffsetX,
                onFloatOffsetXChange = { newValue ->
                    floatOffsetX = newValue
                    prefs.edit().putFloat("float_offset_x", newValue).apply()
                },
                floatOffsetY = floatOffsetY,
                onFloatOffsetYChange = { newValue ->
                    floatOffsetY = newValue
                    prefs.edit().putFloat("float_offset_y", newValue).apply()
                },
                externalBadgeEnabled = externalBadgeEnabled,
                onToggleExternalBadge = { newValue ->
                    externalBadgeEnabled = newValue
                    prefs.edit().putBoolean("external_badge_enabled", newValue).apply()
                    AppLogger.i("设置 外置小图: $newValue")
                },
                followSystemTheme = followSystemTheme,
                onToggleFollowSystemTheme = { newValue ->
                    onFollowSystemThemeChange?.invoke(newValue)
                    AppLogger.i("设置 默认主题: $newValue")
                },
                onShowLanguageDialog = {
                    showProgramSettingsDialog = false
                    showLanguageDialog = true
                },
                disableCustomFont = disableCustomFont,
                onToggleDisableCustomFont = { newValue ->
                    onDisableCustomFontChange?.invoke(newValue)
                },
                nameBelowImage = nameBelowImage,
                onToggleNameBelowImage = { newValue ->
                    nameBelowImage = newValue
                    prefs.edit().putBoolean("name_below_image", newValue).apply()
                    AppLogger.i("设置 下置命名: $newValue")
                }
            )
        }

        // 资源管理对话框
        if (showResourceManageDialog) {
            ResourceManageDialog(
                onDismiss = { showResourceManageDialog = false },
                presetManager = presetManager,
                onResetTemplate = {
                    // 重置为默认模板
                    tierListTitle = context.getString(R.string.default_title)
                    authorName = ""
                    tiers.clear()
                    tiers.addAll(defaultTiers)
                    tierImages.clear()
                    // 清空层级位置信息,确保使用默认模板的层级标签
                    tierRowPositions = emptyMap()
                    pendingImages = emptyList()
                    // 清理裁剪设置
                    prefs.edit()
                        .remove("crop_ratio")
                        .remove("custom_crop_width")
                        .remove("custom_crop_height")
                        .remove("use_custom_crop_size")
                        .apply()
                }
            )
        }

        // 管理图包对话框
        if (showManagePackagesDialog) {
            ManagePackagesDialog(
                context = context,
                presetManager = presetManager,
                onDismiss = { showManagePackagesDialog = false },
                onImportPackage = { showImportPackageDialog = true },
                onPackageSelected = { packageItem ->
                    selectedPackage = packageItem
                    AppLogger.i("选择图包: ${packageItem.name}")
                    // 计算图包中的图片数量
                    scope.launch {
                        selectedPackageImageCount = ResourcePackageManager.countImagesInImportedPackage(packageItem.file)
                        showManagePackagesDialog = false
                        showPackageConfirmDialog = true
                    }
                },
                onExportPackage = { packageItem ->
                    packageToExport = packageItem
                    // 跳过草稿保存标记
                    onSkipDraftSave?.invoke()
                    // 启动文件选择器
                    packageExportLauncher.launch("${packageItem.name}.zip")
                }
            )
        }

        // 图包确认对话框
        if (showPackageConfirmDialog && selectedPackage != null) {
            PackageConfirmDialog(
                packageName = selectedPackage!!.name,
                imageCount = selectedPackageImageCount,
                isImporting = isImportingPackage,
                onDismiss = { 
                    if (!isImportingPackage) {
                        showPackageConfirmDialog = false
                        selectedPackage = null
                    }
                },
                onConfirm = { target ->
                    if (isImportingPackage) return@PackageConfirmDialog
                    isImportingPackage = true
                    scope.launch {
                        try {
                            val targetDir = when (target) {
                                ImportTarget.PENDING -> File(presetManager.getWorkImagesDirectory(), PresetManager.IMAGES_FOLDER_NAME)
                                ImportTarget.BADGES -> File(presetManager.getWorkImagesDirectory(), PresetManager.BADGES_FOLDER_NAME)
                            }
                            
                            val imageUris = ResourcePackageManager.extractImportedPackage(context, selectedPackage!!.file, targetDir)
                            
                            if (imageUris.isNotEmpty()) {
                                when (target) {
                                    ImportTarget.PENDING -> {
                                        pendingImages = pendingImages + imageUris
                                        AppLogger.i("导入图包到待分级区域: ${selectedPackage?.name} - ${imageUris.size} 张图片")
                                        showToastWithoutIcon(
                                            context,
                                            context.getString(R.string.import_success, imageUris.size)
                                        )
                                    }
                                    ImportTarget.BADGES -> {
                                        AppLogger.i("导入图包到小图标区域: ${selectedPackage?.name} - ${imageUris.size} 个")
                                        showToastWithoutIcon(
                                            context,
                                            context.getString(R.string.badge_added)
                                        )
                                    }
                                }
                            } else {
                                showToastWithoutIcon(
                                    context,
                                    resources.getString(R.string.no_images_in_zip)
                                )
                            }
                        } catch (e: Exception) {
                            AppLogger.e("导入图包失败", e)
                            showToastWithoutIcon(
                                context,
                                resources.getString(R.string.import_failed, e.message),
                                Toast.LENGTH_LONG
                            )
                        }
                        isImportingPackage = false
                        showPackageConfirmDialog = false
                        selectedPackage = null
                    }
                }
            )
        }

        // 导入图包对话框
        if (showImportPackageDialog) {
            ImportPackageDialog(
                context = context,
                presetManager = presetManager,
                pendingImages = pendingImages,
                onPendingImagesChanged = { pendingImages = it },
                onDismiss = { showImportPackageDialog = false },
                onSkipDraftSave = onSkipDraftSave,
                onResumeDraftSave = onResumeDraftSave
            )
        }

        // 语言选择对话框
        if (showLanguageDialog) {
            LanguageSelectionDialog(
                currentLanguage = currentLanguage,
                onDismiss = { showLanguageDialog = false },
                onLanguageSelected = { language ->
                    if (language != currentLanguage) {
                        currentLanguage = language
                        languageChanged = true
                        AppLogger.i("切换语言: $language")
                    }
                }
            )
        }

        // 使用说明对话框
        if (showInstructionsDialog) {
            InstructionsDialog(
                onDismiss = { showInstructionsDialog = false }
            )
        }

        // 关于程序对话框
        if (showAboutDialog) {
            AboutDialog(
                onDismiss = { showAboutDialog = false },
                context = context
            )
        }

        // 管理预设对话框
        if (showManagePresetsDialog) {
            ManagePresetsDialog(
                onDismiss = { showManagePresetsDialog = false },
                onExportPreset = {
                    showManagePresetsDialog = false
                    presetOperation = PresetOperation.EXPORT
                    // 检查标题是否为默认值
                    if (tierListTitle == resources.getString(R.string.default_title)) {
                        showPresetNameDialog = true
                    } else {
                        pendingPresetName = tierListTitle
                        onSkipDraftSave?.invoke()
                        presetExportLauncher.launch("${tierListTitle}.tdds")
                    }
                },
                onImportPreset = {
                    showManagePresetsDialog = false
                    onSkipDraftSave?.invoke()
                    presetFilePicker.launch("*/*")
                },
                onSavePreset = {
                    showManagePresetsDialog = false
                    presetOperation = PresetOperation.SAVE
                    // 检查标题是否为默认值
                    if (tierListTitle == resources.getString(R.string.default_title)) {
                        showPresetNameDialog = true
                    } else {
                        pendingPresetName = tierListTitle
                        showPresetOverwriteConfirmDialog = true
                    }
                },
                onManagePresetList = {
                    showManagePresetsDialog = false
                    showPresetListDialog = true
                }
            )
        }

        // 预设名称输入对话框
        if (showPresetNameDialog) {
            PresetNameDialog(
                defaultName = if (tierListTitle == resources.getString(R.string.default_title)) "" else tierListTitle,
                onDismiss = { showPresetNameDialog = false },
                onConfirm = { name ->
                    showPresetNameDialog = false
                    pendingPresetName = name
                    when (presetOperation) {
                        PresetOperation.EXPORT -> {
                            onSkipDraftSave?.invoke()
                            presetExportLauncher.launch("$name.tdds")
                        }
                        PresetOperation.SAVE -> {
                            showPresetOverwriteConfirmDialog = true
                        }
                        else -> {}
                    }
                }
            )
        }

        // 预设覆盖确认对话框
        if (showPresetOverwriteConfirmDialog) {
            val isNameExists = presetManager.isPresetNameExists(pendingPresetName)
            if (isNameExists) {
                // 使用自定义Dialog实现三个按钮
                Dialog(onDismissRequest = { showPresetOverwriteConfirmDialog = false }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .padding(16.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = extendedColors.cardBackground
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 20.dp, horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // 标题
                            Text(
                                text = stringResource(R.string.preset_name_exists),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // 提示文本
                            Text(
                                text = stringResource(R.string.preset_name_exists_message, pendingPresetName),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            // 按钮区域
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // 覆盖按钮
                                Button(
                                    onClick = {
                                        showPresetOverwriteConfirmDialog = false
                                        isSavingPreset = true
                                        scope.launch {
                                            // 让出时间片，确保UI有时间显示加载对话框
                                            yield()
                                            try {
                                                // 在后台线程执行耗时操作
                                                val presetData = withContext(Dispatchers.IO) {
                                                    presetManager.createPresetData(
                                                        title = pendingPresetName,
                                                        author = authorName,
                                                        tiers = tiers,
                                                        tierImages = tierImages,
                                                        pendingImages = pendingImages,
                                                        cropPositionX = prefs.getFloat("crop_position_x", 0.5f),
                                                        cropPositionY = prefs.getFloat("crop_position_y", 0.5f),
                                                        customCropWidth = prefs.getInt("custom_crop_width", 0),
                                                        customCropHeight = prefs.getInt("custom_crop_height", 0),
                                                        useCustomCropSize = prefs.getBoolean("use_custom_crop_size", false),
                                                        cropRatio = prefs.getFloat("crop_ratio", 1f)
                                                    )
                                                }
                                                withContext(Dispatchers.IO) {
                                                    presetManager.savePreset(pendingPresetName, presetData)
                                                }

                                                showToastWithoutIcon(context, resources.getString(R.string.preset_save_success))
                                                AppLogger.i("覆盖预设成功: $pendingPresetName")
                                            } catch (e: Exception) {
                                                AppLogger.e("覆盖预设失败", e)
                                            showToastWithoutIcon(
                                                context,
                                                resources.getString(R.string.preset_save_failed, e.message),
                                                Toast.LENGTH_LONG
                                            )
                                            }
                                            isSavingPreset = false
                                            pendingPresetName = ""
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = extendedColors.buttonContainer,
                                        contentColor = extendedColors.buttonContent
                                    )
                                ) {
                                    Text(stringResource(R.string.overwrite))
                                }
                                
                                // 创建新预设按钮
                                OutlinedButton(
                                    onClick = {
                                        // 关闭覆盖对话框，显示名称输入对话框让用户输入新名称
                                        showPresetOverwriteConfirmDialog = false
                                        showPresetNameDialog = true
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(stringResource(R.string.create_new_preset))
                                }
                            }
                        }
                    }
                }
            } else {
                // 直接保存
                showPresetOverwriteConfirmDialog = false
                isSavingPreset = true
                scope.launch {
                    // 让出时间片，确保UI有时间显示加载对话框
                    yield()
                    try {
                        val presetData = presetManager.createPresetData(
                            title = pendingPresetName,
                            author = authorName,
                            tiers = tiers,
                            tierImages = tierImages,
                            pendingImages = pendingImages,
                            cropPositionX = prefs.getFloat("crop_position_x", 0.5f),
                            cropPositionY = prefs.getFloat("crop_position_y", 0.5f),
                            customCropWidth = prefs.getInt("custom_crop_width", 0),
                            customCropHeight = prefs.getInt("custom_crop_height", 0),
                            useCustomCropSize = prefs.getBoolean("use_custom_crop_size", false),
                            cropRatio = prefs.getFloat("crop_ratio", 1f)
                        )
                        presetManager.savePreset(pendingPresetName, presetData)

                        showToastWithoutIcon(context, resources.getString(R.string.preset_save_success))
                        AppLogger.i("保存预设成功: $pendingPresetName")
                    } catch (e: Exception) {
                        AppLogger.e("保存预设失败", e)
                        showToastWithoutIcon(
                            context,
                            resources.getString(R.string.preset_save_failed, e.message),
                            Toast.LENGTH_LONG
                        )
                    }
                    isSavingPreset = false
                    pendingPresetName = ""
                }
            }
        }

        // 加载资源中对话框（导入/导出/保存预设/导出图包）
        if (isImportingPreset || isExportingPreset || isSavingPreset || isExportingPackage) {
            LoadingDialog(message = stringResource(R.string.loading_resources))
        }

        // 导入预设覆盖确认对话框
        if (showImportOverwriteDialog && pendingImportResult != null) {
            ImportOverwriteDialog(
                presetName = pendingImportResult?.presetData?.title ?: "",
                onDismiss = {
                    showImportOverwriteDialog = false
                    pendingImportResult?.presetFile?.delete()
                    pendingImportResult = null
                },
                onOverwrite = {
                    val importResult = pendingImportResult
                    showImportOverwriteDialog = false
                    pendingImportResult = null

                    importResult?.let { result ->
                        scope.launch {
                            try {
                                // 覆盖现有预设，获取新的预设文件
                                val newPresetFile = result.existingPresetFile?.let { existingFile ->
                                    presetManager.overwritePreset(
                                        result.presetFile,
                                        existingFile
                                    )
                                } ?: result.presetFile

                                // 应用预设数据（使用新的预设文件）
                                val applyResult = presetManager.applyPreset(newPresetFile)
                                // 更新UI状态
                                tiers.clear()
                                tiers.addAll(applyResult.tiers.map { tierData ->
                                    TierItem(tierData.label, try {
                                        Color(android.graphics.Color.parseColor("#${tierData.color}"))
                                    } catch (e: Exception) { Color.Gray })
                                })
                                tierImages.clear()
                                tierImages.addAll(applyResult.tierImages.map { appliedImage ->
                                    TierImage(
                                        id = appliedImage.id,
                                        tierLabel = appliedImage.tierLabel,
                                        uri = appliedImage.uri,
                                        name = appliedImage.name,
                                        badgeUri = appliedImage.badgeUri,
                                        badgeUri2 = appliedImage.badgeUri2,
                                        badgeUri3 = appliedImage.badgeUri3,
                                        originalUri = appliedImage.originalUri,
                                        cropPositionX = appliedImage.cropPositionX,
                                        cropPositionY = appliedImage.cropPositionY,
                                        cropScale = appliedImage.cropScale,
                                        isCropped = appliedImage.isCropped,
                                        cropRatio = appliedImage.cropRatio,
                                        useCustomCrop = appliedImage.useCustomCrop,
                                        customCropWidth = appliedImage.customCropWidth,
                                        customCropHeight = appliedImage.customCropHeight
                                    )
                                })
                                pendingImages = applyResult.pendingImages
                                tierListTitle = result.presetData.title
                                authorName = result.presetData.author
                                // 清理旧的裁剪设置并应用新的
                                prefs.edit()
                                    .remove("crop_ratio")
                                    .remove("custom_crop_width")
                                    .remove("custom_crop_height")
                                    .remove("use_custom_crop_size")
                                    .putInt("custom_crop_width", applyResult.customCropWidth)
                                    .putInt("custom_crop_height", applyResult.customCropHeight)
                                    .putBoolean("use_custom_crop_size", applyResult.useCustomCropSize)
                                    .putFloat("crop_ratio", applyResult.cropRatio)
                                    .apply()
                                // 清空层级位置信息,确保使用预设中的层级标签
                                tierRowPositions = emptyMap()

                                showToastWithoutIcon(context, resources.getString(R.string.preset_overwrite_success))
                                AppLogger.i("覆盖并加载预设成功: ${result.presetData.title}")

                                // 静默保存覆盖原预设文件（转换为WebP格式）
                                withContext(Dispatchers.IO) {
                                    try {
                                        val workImagesDir = presetManager.getWorkImagesDirectory()
                                        val newPresetData = presetManager.createPresetData(
                                            title = tierListTitle,
                                            author = authorName,
                                            tiers = tiers,
                                            tierImages = tierImages,
                                            pendingImages = pendingImages,
                                            cropPositionX = prefs.getFloat("crop_position_x", 0.5f),
                                            cropPositionY = prefs.getFloat("crop_position_y", 0.5f),
                                            customCropWidth = prefs.getInt("custom_crop_width", 0),
                                            customCropHeight = prefs.getInt("custom_crop_height", 0),
                                            useCustomCropSize = prefs.getBoolean("use_custom_crop_size", false),
                                            cropRatio = prefs.getFloat("crop_ratio", 1f)
                                        )
                                        presetManager.exportPreset(
                                            presetName = tierListTitle,
                                            presetData = newPresetData,
                                            tempDir = workImagesDir,
                                            outputFile = newPresetFile
                                        )
                                        AppLogger.i("静默覆盖预设成功: ${newPresetFile.name}")
                                    } catch (e: Exception) {
                                        AppLogger.e("静默覆盖预设失败", e)
                                    }
                                }
                            } catch (e: Exception) {
                                AppLogger.e("覆盖预设失败", e)
                                showToastWithoutIcon(
                                    context,
                                    resources.getString(R.string.preset_overwrite_failed, e.message),
                                    Toast.LENGTH_LONG
                                )
                            }
                        }
                    }
                }
            )
        }

        // 预设列表对话框
        if (showPresetListDialog) {
            val presetImportSuccessMsg = stringResource(R.string.preset_import_success)
            val presetImportFailedMsg = stringResource(R.string.preset_import_failed)
            PresetListDialog(
                presetManager = presetManager,
                onDismiss = { showPresetListDialog = false },
                onApplyPreset = { presetInfo ->
                    // 注意：此函数在 PresetListDialog 的协程中调用，不需要再启动新协程
                    try {
                        val applyResult = presetManager.applyPreset(presetInfo.file)
                        // 更新UI状态
                        tiers.clear()
                        tiers.addAll(applyResult.tiers.map { tierData ->
                            TierItem(tierData.label, try {
                                Color(android.graphics.Color.parseColor("#${tierData.color}"))
                            } catch (e: Exception) { Color.Gray })
                        })
                        tierImages.clear()
                        tierImages.addAll(applyResult.tierImages.map { appliedImage ->
                            TierImage(
                                id = appliedImage.id,
                                tierLabel = appliedImage.tierLabel,
                                uri = appliedImage.uri,
                                name = appliedImage.name,
                                badgeUri = appliedImage.badgeUri,
                                badgeUri2 = appliedImage.badgeUri2,
                                badgeUri3 = appliedImage.badgeUri3,
                                originalUri = appliedImage.originalUri,
                                cropPositionX = appliedImage.cropPositionX,
                                cropPositionY = appliedImage.cropPositionY,
                                cropScale = appliedImage.cropScale,
                                isCropped = appliedImage.isCropped,
                                cropRatio = appliedImage.cropRatio,
                                useCustomCrop = appliedImage.useCustomCrop,
                                customCropWidth = appliedImage.customCropWidth,
                                customCropHeight = appliedImage.customCropHeight
                            )
                        })
                        pendingImages = applyResult.pendingImages
                        tierListTitle = applyResult.title
                        authorName = applyResult.author
                        // 清空层级位置信息,确保使用预设中的层级标签
                        tierRowPositions = emptyMap()
                        // 清理旧的裁剪设置并应用新的
                        prefs.edit()
                            .remove("crop_ratio")
                            .remove("custom_crop_width")
                            .remove("custom_crop_height")
                            .remove("use_custom_crop_size")
                            .putFloat("crop_position_x", applyResult.cropPositionX)
                            .putFloat("crop_position_y", applyResult.cropPositionY)
                            .putInt("custom_crop_width", applyResult.customCropWidth)
                            .putInt("custom_crop_height", applyResult.customCropHeight)
                            .putBoolean("use_custom_crop_size", applyResult.useCustomCropSize)
                            .putFloat("crop_ratio", applyResult.cropRatio)
                            .apply()
                        // 注意：不要在这里关闭对话框，让PresetListDialog在finally块中处理
                        // 这样可以确保加载状态正确显示
                        showToastWithoutIcon(context, presetImportSuccessMsg)
                        AppLogger.i("应用预设成功: ${presetInfo.name}")
                    } catch (e: Exception) {
                        AppLogger.e("应用预设失败", e)
                        showToastWithoutIcon(
                            context,
                            String.format(presetImportFailedMsg, e.message),
                            Toast.LENGTH_LONG
                        )
                        // 发生错误时保持对话框打开，让用户知道失败了
                        throw e
                    }
                }
            )
        }

        // 删除层级确认对话框
        if (showDeleteTierDialog && tierToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteTierDialog = false },
                containerColor = extendedColors.cardBackground,
                title = { Text(stringResource(R.string.delete_tier)) },
                text = { Text(stringResource(R.string.delete_tier_confirm, tierToDelete!!.label)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val index = tiers.indexOfFirst { it.label == tierToDelete!!.label }
                            if (index != -1) {
                                // 将该层级的所有图片返回到待放置区（避免重复，返回原图）
                                val imagesToReturn = tierImages.filter { it.tierLabel == tierToDelete!!.label }
                                val newPendingImages = imagesToReturn.map { it.originalUri ?: it.uri }
                                    .filter { it !in pendingImages }
                                if (newPendingImages.isNotEmpty()) {
                                    pendingImages = pendingImages + newPendingImages
                                }
                                val returnedCount = newPendingImages.size
                                val deletedCount = imagesToReturn.size
                                tiers.removeAt(index)
                                tierImages.removeAll { it.tierLabel == tierToDelete!!.label }
                                // 从 tierRowPositions 中移除该层级的位置信息
                                tierRowPositions = tierRowPositions - tierToDelete!!.label
                                AppLogger.i("删除层级 - 层级: ${tierToDelete!!.label}, 删除图片: ${deletedCount}张, 返回待放置区: ${returnedCount}张")
                            }
                            showDeleteTierDialog = false
                            tierToDelete = null
                        }
                    ) {
                        Text(stringResource(R.string.delete), color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteTierDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }

        // 图片预览对话框
        if (showPreviewDialog && previewBitmap != null) {
            PreviewDialog(
                bitmap = previewBitmap!!,
                isSaving = isSavingChart,
                isSharing = isSharingChart,
                onDismiss = {
                    showPreviewDialog = false
                    previewBitmap = null
                },
                onSave = {
                    isSavingChart = true
                    scope.launch {
                        try {
                            saveBitmapToGallery(context, previewBitmap!!, tierListTitle)
                            showPreviewDialog = false
                            previewBitmap = null
                        } finally {
                            isSavingChart = false
                        }
                    }
                },
                onShare = {
                    isSharingChart = true
                    scope.launch {
                        try {
                            shareBitmap(context, previewBitmap!!, tierListTitle)
                        } finally {
                            isSharingChart = false
                        }
                    }
                },
                onThemeToggle = {
                    // 切换预览主题
                    scope.launch {
                        previewIsDarkTheme = !previewIsDarkTheme
                        previewBitmap = generateTierListBitmap(
                            context, tiers, tierImages, tierListTitle, authorName, previewIsDarkTheme, externalBadgeEnabled, disableCustomFont, nameBelowImage
                        )
                    }
                },
                isDarkTheme = isDarkTheme,
                appDarkTheme = previewIsDarkTheme
            )
        }

        // Toast宿主 - 显示全局Toast提示（使用Popup确保在最上层，覆盖对话框）
        ToastHost(isDarkTheme = isDarkTheme)
    }
}

/**
 * 待分级角色（图片待选区/待放置图片区域）
 * 显示待添加到层级的图片，支持左右滑动选择，向下拖动放置到层级，向上拖动删除
 */
@Composable
fun PendingImagesSection(
    images: List<Uri>,
    tiers: List<TierItem>,
    tierRowPositions: Map<String, android.graphics.Rect>,
    imageSize: Dp,
    imageCornerRadius: Dp,
    onClear: () -> Unit,
    onAdd: () -> Unit = {},
    onDragStart: (Uri) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDropOnTier: (Uri, String) -> Unit,
    onDeleteImage: (Uri) -> Unit = {},
    floatOffsetX: Float = 125f,
    floatOffsetY: Float = 85f,
    onPositionUpdate: ((android.graphics.Rect) -> Unit)? = null
) {
    // 计算待放置区域的高度（基于图片尺寸）
    val pendingSectionHeight = (imageSize.value * 0.8f + 8f).dp
    // 拖动状态
    var isDragging by remember { mutableStateOf(false) }
    var draggedUri by remember { mutableStateOf<Uri?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    var currentDropTarget by remember { mutableStateOf<String?>(null) }
    // 是否处于删除模式
    var isDeleteMode by remember { mutableStateOf(false) }

    // 使用 BoxWithConstraints 获取父容器尺寸
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        val extendedColors = LocalExtendedColors.current
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(top = 4.dp, bottom = 8.dp)
                .onGloballyPositioned { coordinates ->
                    onPositionUpdate?.let { update ->
                        val bounds = coordinates.boundsInWindow()
                        val rect = android.graphics.Rect(
                            bounds.left.toInt(),
                            bounds.top.toInt(),
                            bounds.right.toInt(),
                            bounds.bottom.toInt()
                        )
                        update(rect)
                    }
                },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = extendedColors.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp).padding(top = 6.dp, bottom = 10.dp)
            ) {
                // 待分级角色标题、添加按钮和清空按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.character_pool, images.size),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onAdd,
                            modifier = Modifier.padding(0.dp)
                        ) {
                            Text(
                                stringResource(R.string.add),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        TextButton(
                            onClick = onClear,
                            modifier = Modifier.padding(0.dp)
                        ) {
                            Text(
                                stringResource(R.string.clear),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // 提示文本
                Text(
                    text = stringResource(R.string.click_to_add_images),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )

                // 图片待选区 - 待放置图片列表
                LazyRow(
                    modifier = Modifier.height(pendingSectionHeight)
                ) {
                    items(images, key = { it.toString() }) { uri ->
                        DraggablePendingImageItem(
                            uri = uri,
                            imageSize = imageSize,
                            imageCornerRadius = imageCornerRadius,
                            isDragging = isDragging && draggedUri == uri,
                            tiers = tiers,
                            tierRowPositions = tierRowPositions,
                            onDragStart = { uriItem, initialCenter ->
                                isDragging = true
                                draggedUri = uriItem
                                dragPosition = initialCenter
                                isDeleteMode = false
                                onDragStart(uriItem)
                            },
                            onDrag = { currentCenter, dropTarget, deleteMode ->
                                dragPosition = currentCenter
                                currentDropTarget = dropTarget
                                isDeleteMode = deleteMode
                            },
                            onDragEnd = { finalDropTarget, deleteMode ->
                                if (deleteMode) {
                                    // 删除模式，从待选区移除图片
                                    draggedUri?.let { uri ->
                                        onDeleteImage(uri)
                                    }
                                } else {
                                    // 如果有目标层级，添加到该层级
                                    finalDropTarget?.let { tierLabel ->
                                        draggedUri?.let { uri ->
                                            onDropOnTier(uri, tierLabel)
                                        }
                                    }
                                }
                                isDragging = false
                                draggedUri = null
                                dragPosition = Offset.Zero
                                currentDropTarget = null
                                isDeleteMode = false
                                onDragEnd()
                            }
                        )
                    }
                }
            }
        }

        // 浮动显示的被拖动图片（全屏层）
        if (isDragging && draggedUri != null) {
            FloatingDragImage(
                uri = draggedUri!!,
                position = dragPosition,
                dropTarget = currentDropTarget,
                floatOffsetX = floatOffsetX,
                floatOffsetY = floatOffsetY,
                isDeleteMode = isDeleteMode
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeableTierRow(
    tier: TierItem,
    images: List<TierImage>,
    pendingImages: List<Uri>,
    tierLabelWidth: Dp,
    imageSize: Dp,
    tierRowHeight: Dp,
    labelFontSize: TextUnit,
    tierLabelCornerRadius: Dp,
    imageCornerRadius: Dp,
    selectedImageForDrag: TierImage?,
    onTierClick: () -> Unit,
    onTierLongClick: () -> Unit,
    onTierDoubleClick: () -> Unit,
    onAddImage: (Uri) -> Unit,
    onImageClick: (TierImage, Int) -> Unit,
    onImageLongClick: (TierImage, Int) -> Unit,
    onImageDoubleClick: (TierImage, Int) -> Unit,
    onDeleteTier: () -> Unit,
    onPickImage: () -> Unit,
    onPositionUpdate: ((String, android.graphics.Rect) -> Unit)? = null,
    disableClickAdd: Boolean = false,
    isDraggingPendingImage: Boolean = false,
    onMoveSelectedImageToTier: (() -> Unit)? = null,
    // 层级图片拖拽相关参数
    tierRowPositions: Map<String, android.graphics.Rect> = emptyMap(),
    pendingSectionRect: android.graphics.Rect? = null,
    draggingTierImage: TierImage? = null,
    onTierImageDragStart: ((TierImage, Offset) -> Unit)? = null,
    onTierImageDrag: ((Offset, String?, Boolean, Boolean) -> Unit)? = null,
    onTierImageDragEnd: ((TierImage, String?, Boolean, Boolean) -> Unit)? = null,
    // 层级顺序调整相关参数
    onTierReorder: ((fromIndex: Int, toIndex: Int) -> Unit)? = null,
    tierIndex: Int = 0,
    totalTiers: Int = 0,
    allTierLabels: List<String> = emptyList(), // 所有层级标签列表，用于正确查找目标索引
    // 列表状态用于自动滚动
    listState: androidx.compose.foundation.lazy.LazyListState? = null
) {
    // 使用 tier.label 作为 key，确保每个层级有独立的滑动状态
    var offsetX by remember(tier.label) { mutableFloatStateOf(0f) }
    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        label = ""
    )

    // 获取屏幕宽高计算滑动阈值和自动滚动
    val configuration = LocalContext.current.resources.configuration
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val maxDragDistance = screenWidth.value * 0.75f // 最大滑动距离为屏幕宽度的75%
    val deleteThreshold = screenWidth.value * 0.5f // 删除阈值为屏幕宽度的50%

    val extendedColors = LocalExtendedColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .height(tierRowHeight)
            .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
            .onGloballyPositioned { coordinates ->
                // 报告层级位置
                onPositionUpdate?.let { update ->
                    val bounds = coordinates.boundsInWindow()
                    val rect = android.graphics.Rect(
                        bounds.left.toInt(),
                        bounds.top.toInt(),
                        bounds.right.toInt(),
                        bounds.bottom.toInt()
                    )
                    update(tier.label, rect)
                }
            }
            .pointerInput(tier.label) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (offsetX > deleteThreshold) {
                            // 向右滑动超过阈值（超过屏幕宽度的50%），直接删除
                            offsetX = 0f
                            onDeleteTier()
                        } else {
                            // 滑动未超过阈值，回弹
                            offsetX = 0f
                        }
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        val newOffset = offsetX + dragAmount
                        // 限制只能向右滑动，最大为屏幕宽度的75%
                        offsetX = newOffset.coerceIn(0f, maxDragDistance)
                    }
                )
            }
    ) {
        // 左侧层级标签（支持拖拽排序）
        val density = LocalDensity.current
        val labelScope = rememberCoroutineScope()
        var labelCenterGlobal by remember { mutableStateOf(Offset.Zero) }
        // 是否正在拖拽此层级
        var isThisDragging by remember { mutableStateOf(false) }
        // 当前手指位置对应的目标层级索引
        var dragTargetIndex by remember { mutableStateOf(-1) }
        
        // 点击/长按/双击状态
        var lastLabelClickTime by remember { mutableLongStateOf(0L) }
        var lastLabelClickPosition by remember { mutableStateOf(Offset.Zero) }
        val labelDoubleClickThresholdMs = 300L
        
        Box(
            modifier = Modifier
                .width(tierLabelWidth)
                .fillMaxHeight()
                .onGloballyPositioned { coordinates ->
                    val bounds = coordinates.boundsInWindow()
                    labelCenterGlobal = Offset(
                        bounds.left + bounds.width / 2,
                        bounds.top + bounds.height / 2
                    )
                }
                // 如果是拖拽目标，显示蓝色边框
                .then(
                    if (dragTargetIndex == tierIndex && !isThisDragging) {
                        Modifier.border(3.dp, Color(0xFF2196F3), RoundedCornerShape(tierLabelCornerRadius))
                    } else Modifier
                )
                .background(
                    if (isThisDragging) tier.color.copy(alpha = 0.5f) else tier.color,
                    RoundedCornerShape(tierLabelCornerRadius)
                )
                .pointerInput(tierIndex, totalTiers) {
                    awaitPointerEventScope {
                        while (true) {
                            val down = awaitFirstDown()
                            val startPosition = down.position
                            val downTime = System.currentTimeMillis()
                            var gestureType: GestureType? = null
                            
                            down.consume()
                            
                            while (gestureType == null) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull() ?: break
                                
                                if (change.changedToUpIgnoreConsumed()) {
                                    // 手指抬起，处理点击/双击
                                    val upTime = System.currentTimeMillis()
                                    val timeSinceLastClick = upTime - lastLabelClickTime
                                    val distanceFromLastClick = (startPosition - lastLabelClickPosition).getDistance()
                                    
                                    if (timeSinceLastClick < labelDoubleClickThresholdMs && distanceFromLastClick < 50f) {
                                        onTierDoubleClick()
                                        lastLabelClickTime = 0L
                                    } else {
                                        lastLabelClickTime = upTime
                                        lastLabelClickPosition = startPosition
                                        
                                        labelScope.launch {
                                            delay(labelDoubleClickThresholdMs)
                                            if (lastLabelClickTime == upTime) {
                                                onTierClick()
                                            }
                                        }
                                    }
                                    break
                                }
                                
                                val currentPosition = change.position
                                val deltaY = currentPosition.y - startPosition.y
                                val deltaX = currentPosition.x - startPosition.x
                                val pressDuration = System.currentTimeMillis() - downTime
                                val moveDistance = kotlin.math.hypot(deltaX, deltaY)
                                
                                // 检测手势类型
                                gestureType = when {
                                    // 优先检测垂直拖动（直接上下拖拽即可触发层级排序）
                                    kotlin.math.abs(deltaY) > 40f && kotlin.math.abs(deltaY) > kotlin.math.abs(deltaX) -> {
                                        isThisDragging = true
                                        // 层级排序拖拽不触发浮动图片显示
                                        change.consume()
                                        GestureType.VerticalDrag
                                    }
                                    // 水平滑动检测
                                    kotlin.math.abs(deltaX) > 40f -> {
                                        GestureType.HorizontalSwipe
                                    }
                                    // 长按检测：时间达到阈值且移动距离很小（纯长按）
                                    pressDuration >= 400L && moveDistance < 25f -> {
                                        onTierLongClick()
                                        GestureType.LongPress
                                    }
                                    else -> null
                                }
                            }
                            
                            // 根据手势类型处理后续事件
                            when (gestureType) {
                                GestureType.LongPress -> {
                                    // 长按后等待手指抬起
                                    while (true) {
                                        val waitEvent = awaitPointerEvent()
                                        val waitChange = waitEvent.changes.firstOrNull()
                                        if (waitChange?.changedToUpIgnoreConsumed() == true) {
                                            waitChange.consume()
                                            break
                                        }
                                        waitChange?.consume()
                                    }
                                }
                                GestureType.VerticalDrag -> {
                                    // 处理垂直拖动
                                    while (true) {
                                        val dragEvent = awaitPointerEvent()
                                        val dragChange = dragEvent.changes.firstOrNull() ?: break
                                        
                                        if (dragChange.changedToUpIgnoreConsumed()) {
                                            // 使用手指松开时的最终位置计算目标层级
                                            val currentPos = dragChange.position
                                            val finalGlobalPos = labelCenterGlobal + Offset(0f, currentPos.y - startPosition.y)
                                            
                                            // 根据手指最终位置计算目标层级
                                            var finalTargetIndex = -1
                                            for ((idx, label) in allTierLabels.withIndex()) {
                                                val rect = tierRowPositions[label]
                                                if (rect != null && finalGlobalPos.y >= rect.top && finalGlobalPos.y <= rect.bottom) {
                                                    finalTargetIndex = idx
                                                    break
                                                }
                                            }
                                            
                                            // 结束拖动，执行交换
                                            if (finalTargetIndex != -1 && finalTargetIndex != tierIndex) {
                                                onTierReorder?.invoke(tierIndex, finalTargetIndex)
                                            }
                                            
                                            isThisDragging = false
                                            dragTargetIndex = -1
                                            // 层级排序拖拽结束，不触发浮动图片相关回调
                                            break
                                        }
                                        
                                        // 计算当前手指位置对应的目标层级
                                        val currentPos = dragChange.position
                                        val globalPos = labelCenterGlobal + Offset(0f, currentPos.y - startPosition.y)
                                        
                                        // 查找手指当前位于哪个层级（使用 allTierLabels 确保顺序正确）
                                        var newTargetIndex = -1
                                        for ((idx, label) in allTierLabels.withIndex()) {
                                            val rect = tierRowPositions[label]
                                            if (rect != null && globalPos.y >= rect.top && globalPos.y <= rect.bottom) {
                                                newTargetIndex = idx
                                                break
                                            }
                                        }
                                        dragTargetIndex = newTargetIndex
                                        
                                        dragChange.consume()
                                    }
                                }
                                GestureType.HorizontalSwipe -> {
                                    // 水平滑动不处理，让父组件处理
                                }
                                else -> {}
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // 如果被拖拽，显示上下箭头图标
            if (isThisDragging) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.ArrowDropUp,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Text(
                    text = TextUtils.truncateTierLabel(tier.label),
                    fontSize = labelFontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        // 右侧图片区域
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable {
                    // 如果有选中的图片且不在当前层级，移动图片到当前层级
                    if (selectedImageForDrag != null && selectedImageForDrag.tierLabel != tier.label) {
                        onMoveSelectedImageToTier?.invoke()
                    } else if (pendingImages.isEmpty()) {
                        // 点击空白区域打开图片选择器
                        onPickImage()
                    }
                },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = extendedColors.surfaceVariant
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                if (images.isEmpty() && pendingImages.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isDraggingPendingImage) stringResource(R.string.drop_image_here) else stringResource(R.string.click_to_add_image),
                            color = if (isDraggingPendingImage) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)
                    ) {
                        // 已添加的图片
                        // 使用"${index}_${image.id}"作为key,确保位置或内容变化时重新创建
                        itemsIndexed(images, key = { index, image -> "${index}_${image.id}_${image.uri}" }) { index, image ->
                            DraggableImage(
                                image = image,
                                index = index,
                                imageSize = imageSize,
                                imageCornerRadius = imageCornerRadius,
                                isSelected = selectedImageForDrag?.id == image.id,
                                isDragging = draggingTierImage?.id == image.id,
                                onClick = { onImageClick(image, index) },
                                onLongClick = { onImageLongClick(image, index) },
                                onDoubleClick = { onImageDoubleClick(image, index) },
                                onDragStart = { center -> onTierImageDragStart?.invoke(image, center) },
                                onDrag = { center, targetTier, deleteMode, toPending -> onTierImageDrag?.invoke(center, targetTier, deleteMode, toPending) },
                                onDragEnd = { targetTier, deleteMode, toPending -> onTierImageDragEnd?.invoke(image, targetTier, deleteMode, toPending) },
                                tierRowPositions = tierRowPositions,
                                pendingSectionRect = pendingSectionRect,
                                listState = listState
                            )
                        }

                        // 待添加的图片（仅在未禁用加添且未拖拽层级图片时显示）
                        if (!disableClickAdd && draggingTierImage == null) {
                            items(pendingImages) { uri ->
                                Box(
                                    modifier = Modifier
                                        .size(imageSize)
                                        .clickable { onAddImage(uri) }
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(uri)
                                            .crossfade(true)
                                            .diskCachePolicy(CachePolicy.DISABLED)
                                            .build(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(imageCornerRadius)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.4f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = stringResource(R.string.add),
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DraggableImage(
    image: TierImage,
    index: Int,
    imageSize: Dp,
    imageCornerRadius: Dp,
    isSelected: Boolean,
    isDragging: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDoubleClick: () -> Unit,
    onDragStart: ((Offset) -> Unit)? = null,
    onDrag: ((Offset, String?, Boolean, Boolean) -> Unit)? = null,
    onDragEnd: ((String?, Boolean, Boolean) -> Unit)? = null,
    tierRowPositions: Map<String, android.graphics.Rect> = emptyMap(),
    pendingSectionRect: android.graphics.Rect? = null,
    listState: androidx.compose.foundation.lazy.LazyListState? = null
) {
    val badgeSize = (imageSize.value * 0.22f).dp
    val density = LocalDensity.current
    val imageScope = rememberCoroutineScope()
    val context = LocalContext.current
    val configuration = context.resources.configuration
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    
    // 记录图片中心点的全局位置
    var itemCenterGlobal by remember { mutableStateOf(Offset.Zero) }
    // 记录拖动偏移量
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    // 本地拖动状态
    var localIsDragging by remember { mutableStateOf(false) }
    // 是否处于删除模式
    var isDeleteMode by remember { mutableStateOf(false) }
    // 是否在待分级区上方
    var isOverPendingSection by remember { mutableStateOf(false) }
    // 使用 rememberUpdatedState 确保总是使用最新的位置信息
    val currentTierRowPositions by rememberUpdatedState(tierRowPositions)
    val currentPendingSectionRect by rememberUpdatedState(pendingSectionRect)
    val currentListState by rememberUpdatedState(listState)
    // 图片高度（用于判断向上拖动是否超过图片自身高度）
    val itemHeightPx = with(density) { imageSize.toPx() }
    
    // 删除模式检测：向上拖拽超出待分级区顶部
    
    // 点击/长按/双击状态
    var lastClickTime by remember { mutableLongStateOf(0L) }
    var lastClickPosition by remember { mutableStateOf(Offset.Zero) }
    val clickThresholdMs = 200L
    val doubleClickThresholdMs = 300L
    
    Box(
        modifier = Modifier
            .size(imageSize)
            .then(
                if (isDragging || localIsDragging) {
                    Modifier.border(2.dp, if (isDeleteMode) Color(0xFFE91E63) else Color(0xFF2196F3), RoundedCornerShape(imageCornerRadius))
                } else if (isSelected) {
                    Modifier.border(3.dp, Color(0xFF2196F3), RoundedCornerShape(imageCornerRadius))
                } else Modifier
            )
            .onGloballyPositioned { coordinates ->
                val bounds = coordinates.boundsInWindow()
                itemCenterGlobal = Offset(
                    bounds.left + bounds.width / 2,
                    bounds.top + bounds.height / 2
                )
            }
            .pointerInput(Unit) {
                val dragThreshold = 40f
                val longPressThresholdMs = 300L // 缩短为300ms
                // 长按移动阈值：图片大小
                val longPressMoveThresholdPx = with(density) { imageSize.toPx() }
                
                awaitPointerEventScope {
                    while (true) {
                        val down = awaitFirstDown()
                        val startPosition = down.position
                        val downTime = System.currentTimeMillis()
                        var hasDragged = false
                        var hasLongPressed = false
                        var maxMoveDistance = 0f
                        
                        down.consume()
                        
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull() ?: break
                            
                            if (change.changedToUpIgnoreConsumed()) {
                                val upTime = System.currentTimeMillis()
                                
                                if (localIsDragging) {
                                    if (isDeleteMode) {
                                        onDragEnd?.invoke(null, true, false)
                                    } else if (isOverPendingSection) {
                                        onDragEnd?.invoke(null, false, true)
                                    } else {
                                        val finalCenter = itemCenterGlobal + dragOffset
                                        var foundTier: String? = null
                                        for ((tierLabel, rect) in currentTierRowPositions) {
                                            if (finalCenter.x >= rect.left && finalCenter.x <= rect.right &&
                                                finalCenter.y >= rect.top && finalCenter.y <= rect.bottom) {
                                                foundTier = tierLabel
                                                break
                                            }
                                        }
                                        onDragEnd?.invoke(foundTier, false, false)
                                    }
                                    localIsDragging = false
                                    dragOffset = Offset.Zero
                                    isDeleteMode = false
                                    isOverPendingSection = false
                                } else if (!hasDragged && !hasLongPressed) {
                                    val timeSinceLastClick = upTime - lastClickTime
                                    val distanceFromLastClick = (startPosition - lastClickPosition).getDistance()
                                    
                                    if (timeSinceLastClick < doubleClickThresholdMs && distanceFromLastClick < 50f) {
                                        onDoubleClick()
                                        lastClickTime = 0L
                                    } else {
                                        lastClickTime = upTime
                                        lastClickPosition = startPosition
                                        
                                        imageScope.launch {
                                            delay(doubleClickThresholdMs)
                                            if (lastClickTime == upTime) {
                                                onClick()
                                            }
                                        }
                                    }
                                }
                                break
                            }
                            
                            val currentPosition = change.position
                            val deltaY = currentPosition.y - startPosition.y
                            val deltaX = currentPosition.x - startPosition.x
                            val pressDuration = System.currentTimeMillis() - downTime
                            val moveDistance = kotlin.math.hypot(deltaX, deltaY)
                            maxMoveDistance = kotlin.math.max(maxMoveDistance, moveDistance)
                            
                            if (!hasDragged && !hasLongPressed) {
                                // 优先检测垂直拖动（直接上下拖拽即可触发）
                                if (abs(deltaY) > dragThreshold && abs(deltaY) > abs(deltaX)) {
                                    hasDragged = true
                                    dragOffset = Offset.Zero
                                    localIsDragging = true
                                    onDragStart?.invoke(itemCenterGlobal)
                                    change.consume()
                                    continue
                                }
                                
                                // 水平滑动不处理，让父组件处理
                                if (abs(deltaX) > dragThreshold) {
                                    // 如果已经开始拖动，需要取消拖动状态
                                    if (localIsDragging) {
                                        localIsDragging = false
                                        dragOffset = Offset.Zero
                                        isDeleteMode = false
                                        isOverPendingSection = false
                                        onDragEnd?.invoke(null, false, false)
                                    }
                                    break
                                }
                                
                                // 长按功能未实现，继续等待其他手势（单击或双击）
                                continue
                            }
                            
                            if (localIsDragging) {
                                val currentOffset = currentPosition - startPosition
                                dragOffset = currentOffset
                                change.consume()
                                
                                // 检测是否在待分级区上方
                                val currentCenter = itemCenterGlobal + dragOffset
                                isOverPendingSection = currentPendingSectionRect?.let { rect ->
                                    currentCenter.x >= rect.left && currentCenter.x <= rect.right &&
                                    currentCenter.y >= rect.top && currentCenter.y <= rect.bottom
                                } ?: false
                                
                                // 检测是否处于删除模式（向上拖拽超出待分级区顶部）
                                val pendingRect = currentPendingSectionRect
                                isDeleteMode = pendingRect?.let { currentCenter.y < it.top } ?: false
                                
                                var foundTier: String? = null
                                for ((tierLabel, rect) in currentTierRowPositions) {
                                    if (currentCenter.x >= rect.left && currentCenter.x <= rect.right &&
                                        currentCenter.y >= rect.top && currentCenter.y <= rect.bottom) {
                                        foundTier = tierLabel
                                        break
                                    }
                                }
                                onDrag?.invoke(currentCenter, foundTier, isDeleteMode, isOverPendingSection)
                            }
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image.uri)
                .crossfade(true)
                .diskCachePolicy(CachePolicy.DISABLED)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(imageCornerRadius)),
            contentScale = ContentScale.Crop
        )

        // 显示小图标（编辑页面始终内置在右上角）
        if (image.badgeUri != null || image.badgeUri2 != null || image.badgeUri3 != null) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // 小图标1
                if (image.badgeUri != null) {
                    Box(
                        modifier = Modifier.size(badgeSize)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(image.badgeUri)
                                .crossfade(true)
                                .diskCachePolicy(CachePolicy.DISABLED)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                // 小图标2
                if (image.badgeUri2 != null) {
                    Box(
                        modifier = Modifier.size(badgeSize)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(image.badgeUri2)
                                .crossfade(true)
                                .diskCachePolicy(CachePolicy.DISABLED)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                // 小图标3
                if (image.badgeUri3 != null) {
                    Box(
                        modifier = Modifier.size(badgeSize)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(image.badgeUri3)
                                .crossfade(true)
                                .diskCachePolicy(CachePolicy.DISABLED)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
        
        // 显示图片命名（如果有）
        if (image.name.isNotBlank()) {
            val nameFontSize = (9f * (imageSize.value / 70f)).coerceAtLeast(7f).sp
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 2.dp, vertical = 1.dp)
            ) {
                Text(
                    text = TextUtils.truncateImageNameForEdit(image.name),
                    color = Color.White,
                    fontSize = nameFontSize,
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

// 待添加区的图片项（拖动时原位置保持不动）
@Composable
fun DraggablePendingImageItem(
    uri: Uri,
    imageSize: Dp,
    imageCornerRadius: Dp,
    isDragging: Boolean,
    tiers: List<TierItem>,
    tierRowPositions: Map<String, android.graphics.Rect>,
    onDragStart: (Uri, Offset) -> Unit,
    onDrag: (Offset, String?, Boolean) -> Unit,
    onDragEnd: (String?, Boolean) -> Unit
) {
    val density = LocalDensity.current
    // 记录图片中心点的全局位置
    var itemCenterGlobal by remember { mutableStateOf(Offset.Zero) }
    // 记录拖动偏移量
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    // 本地拖动状态（用于长按后启动拖动）
    var localIsDragging by remember { mutableStateOf(false) }
    // 是否处于删除状态（向上拖动超过图片高度）
    var isDeleteMode by remember { mutableStateOf(false) }
    // 使用 rememberUpdatedState 确保总是使用最新的 tierRowPositions
    val currentTierRowPositions by rememberUpdatedState(tierRowPositions)
    // 计算待放置区图片尺寸（比层级中的图片稍小）
    val pendingImageSize = (imageSize.value * 0.8f).dp
    // 图片高度（用于判断向上拖动是否超过图片高度）
    val itemHeightPx = with(density) { pendingImageSize.toPx() }

    Box(
        modifier = Modifier
            .size(pendingImageSize)
            .padding(2.dp)
            .then(
                if (isDragging || localIsDragging) {
                    Modifier.border(2.dp, if (isDeleteMode) Color(0xFFE91E63) else Color(0xFF2196F3), RoundedCornerShape(imageCornerRadius))
                } else Modifier
            )
            .onGloballyPositioned { coordinates ->
                val bounds = coordinates.boundsInWindow()
                // 计算中心点位置（在窗口坐标系中）
                itemCenterGlobal = Offset(
                    bounds.left + bounds.width / 2,
                    bounds.top + bounds.height / 2
                )
            }
            .pointerInput(Unit) {
                // 使用底层 API 处理手势
                // 往下拖动超过此阈值才触发拖放
                val verticalDragThreshold = 20f
                val horizontalDragThreshold = 30f // 水平滑动阈值，超过此值才认为是水平滑动
                
                awaitPointerEventScope {
                    while (true) {
                        // 等待手指按下
                        val down = awaitFirstDown()
                        val startPosition = down.position
                        var dragTriggered = false
                        var isHorizontalScroll = false
                        
                        down.consume()
                        
                        // 持续处理后续事件
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull() ?: break
                            
                            // 手指抬起
                            if (change.changedToUpIgnoreConsumed()) {
                                if (localIsDragging) {
                                    // 结束拖动，如果是删除模式则删除，否则计算放置目标
                                    if (isDeleteMode) {
                                        onDragEnd(null, true)
                                    } else {
                                        // 计算放置目标
                                        val finalCenter = itemCenterGlobal + dragOffset
                                        var foundTier: String? = null
                                        for ((tierLabel, rect) in currentTierRowPositions) {
                                            if (finalCenter.x >= rect.left && finalCenter.x <= rect.right &&
                                                finalCenter.y >= rect.top && finalCenter.y <= rect.bottom) {
                                                foundTier = tierLabel
                                                break
                                            }
                                        }
                                        onDragEnd(foundTier, false)
                                    }
                                    localIsDragging = false
                                    dragOffset = Offset.Zero
                                    isDeleteMode = false
                                }
                                break
                            }
                            
                            // 手指移动
                            val currentPosition = change.position
                            val deltaY = currentPosition.y - startPosition.y
                            val deltaX = currentPosition.x - startPosition.x
                            
                            // 如果还没触发拖动，检测是否往下或往上拖动超过阈值
                            if (!dragTriggered) {
                                // 检测是否为水平滑动（水平移动更多且超过阈值）
                                if (abs(deltaX) > abs(deltaY) && abs(deltaX) > horizontalDragThreshold) {
                                    // 水平滑动，不消费事件，让父组件(LazyRow)处理
                                    isHorizontalScroll = true
                                    break
                                }
                                // 如果往下或往上拖动超过阈值，触发拖放
                                if (deltaY > verticalDragThreshold || deltaY < -verticalDragThreshold) {
                                    dragTriggered = true
                                    dragOffset = Offset.Zero
                                    localIsDragging = true
                                    onDragStart(uri, itemCenterGlobal)
                                    change.consume()
                                }
                                // 移动距离还不够，继续等待
                                continue
                            }
                            
                            // 拖动已触发，处理拖动
                            // 计算相对于起始位置的偏移
                            val currentOffset = currentPosition - startPosition
                            dragOffset = currentOffset
                            change.consume()
                            
                            // 检测是否处于删除模式（向上拖动超过图片自身高度）
                            isDeleteMode = deltaY < -itemHeightPx
                            
                            // 计算当前中心点
                            val currentCenter = itemCenterGlobal + dragOffset
                            var foundTier: String? = null
                            for ((tierLabel, rect) in currentTierRowPositions) {
                                if (currentCenter.x >= rect.left && currentCenter.x <= rect.right &&
                                    currentCenter.y >= rect.top && currentCenter.y <= rect.bottom) {
                                    foundTier = tierLabel
                                    break
                                }
                            }
                            onDrag(currentCenter, foundTier, isDeleteMode)
                        }
                    }
                }
            }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .crossfade(true)
                .diskCachePolicy(CachePolicy.DISABLED)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(imageCornerRadius))
                .then(if (isDragging) Modifier.alpha(0.5f) else Modifier),
            contentScale = ContentScale.Crop
        )
    }
}

// 浮动显示的被拖动图片
@Composable
fun FloatingDragImage(
    uri: Uri,
    position: Offset,
    dropTarget: String?,
    floatOffsetX: Float = 125f,
    floatOffsetY: Float = 85f,
    isDeleteMode: Boolean = false
) {
    val scale by animateFloatAsState(
        targetValue = when {
            isDeleteMode -> 1.1f
            dropTarget != null -> 1.15f
            else -> 1.1f
        },
        label = ""
    )

    // 使用 Popup 在全屏窗口中显示拖动图片，这样不会遮挡层级
    Popup(
        alignment = Alignment.TopStart,
        offset = IntOffset(0, 0),
        properties = PopupProperties(
            focusable = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 使用调节浮显设置的偏移量
            val offsetX = floatOffsetX.dp
            val offsetY = floatOffsetY.dp

            // 图片显示位置：根据滑动条设置的偏移量
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .offset {
                        IntOffset(
                            (position.x - offsetX.toPx()).roundToInt(),
                            (position.y - offsetY.toPx()).roundToInt()
                        )
                    }
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        alpha = 0.95f
                    )
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uri)
                        .crossfade(true)
                        .diskCachePolicy(CachePolicy.DISABLED)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = when {
                                isDeleteMode -> 4.dp
                                dropTarget != null -> 4.dp
                                else -> 3.dp
                            },
                            color = when {
                                isDeleteMode -> Color(0xFFE91E63)
                                dropTarget != null -> Color(0xFF4CAF50)
                                else -> Color(0xFF2196F3)
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .shadow(
                            elevation = if (dropTarget != null) 12.dp else 8.dp,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentScale = ContentScale.Crop
                )

                // 显示删除提示或放置提示
                if (isDeleteMode) {
                    // 删除态视觉反馈
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFE91E63).copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.delete_label),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .background(Color(0xFFE91E63), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else if (dropTarget != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF4CAF50).copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // 限制层级名称显示：中文2个字，非中文3个字符
                        val displayText = if (dropTarget.any { it.code in 0x4E00..0x9FFF }) {
                            // 包含中文字符，限制为2个字
                            dropTarget.take(2)
                        } else {
                            // 非中文，限制为3个字符
                            dropTarget.take(3)
                        }
                        Text(
                            text = displayText,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .background(Color(0xFF4CAF50), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddTierButton(onClick: () -> Unit) {
    val extendedColors = LocalExtendedColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(50.dp)
            .background(
                extendedColors.surfaceVariant,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = stringResource(R.string.add_tier),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun AuthorInfoSection(
    authorName: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (authorName.isBlank()) "-${stringResource(R.string.input_author)}-" else "-$authorName-",
            fontSize = 16.sp,
            color = if (authorName.isBlank()) Color.Gray else MaterialTheme.colorScheme.primary
        )
    }
}



@Composable
fun ImageActionDialog(
    onDismiss: () -> Unit,
    onSetBadge: () -> Unit,
    onReplace: () -> Unit,
    onMove: () -> Unit,
    onRename: () -> Unit,
    onView: () -> Unit,
    onCrop: () -> Unit
) {
    val extendedColors = LocalExtendedColors.current
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = extendedColors.cardBackground,
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.image_action))
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.select_operation))
                Spacer(modifier = Modifier.height(16.dp))
                // 第一行：查看、裁剪、移动
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = onView) {
                        Text(stringResource(R.string.view))
                    }
                    TextButton(onClick = onCrop) {
                        Text(stringResource(R.string.crop))
                    }
                    TextButton(onClick = onMove) {
                        Text(stringResource(R.string.move))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // 第二行：替换、命名、设置小图标
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = onReplace) {
                        Text(stringResource(R.string.replace))
                    }
                    TextButton(onClick = onRename) {
                        Text(stringResource(R.string.rename))
                    }
                    TextButton(onClick = onSetBadge) {
                        Text(stringResource(R.string.set_badges))
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

@Composable
fun MoveImageDialog(
    tiers: List<TierItem>,
    currentTierLabel: String,
    onDismiss: () -> Unit,
    onMoveToTier: (String) -> Unit,
    onMoveToFirst: () -> Unit,
    onMoveToLast: () -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit
) {
    val extendedColors = LocalExtendedColors.current
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = extendedColors.cardBackground,
        title = { Text(stringResource(R.string.move_to_tier)) },
        text = {
            Column {
                Text(stringResource(R.string.select_target_tier))
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(tiers.filter { it.label != currentTierLabel }) { tier ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onMoveToTier(tier.label) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(tier.color, RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tier.label,
                                    fontWeight = FontWeight.Bold,
                                    color = if (ColorUtils.isDarkColor(tier.color)) Color.White else Color.Black,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    fontSize = 12.sp,
                                    lineHeight = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = stringResource(R.string.tier_label, tier.label))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.move_position))
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(
                        onClick = onMoveToFirst
                    ) {
                        Text(stringResource(R.string.move_to_first))
                    }
                    TextButton(
                        onClick = onMoveToLast
                    ) {
                        Text(stringResource(R.string.move_to_last))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(
                        onClick = onMoveLeft
                    ) {
                        Text(stringResource(R.string.move_left))
                    }
                    TextButton(
                        onClick = onMoveRight
                    ) {
                        Text(stringResource(R.string.move_right))
                    }
                }
            }
        },
        confirmButton = {}
    )
}

/**
 * 小图标预览区域 - 独立的Composable以实现局部刷新
 * 当小图标列表变化时,只有这个区域会重新加载,而不会导致整个对话框闪烁
 */
@Composable
fun BadgePreviewArea(
    presetManager: PresetManager,
    selectedBadgeSlot: Int,
    badgeUri1: Uri?,
    badgeUri2: Uri?,
    badgeUri3: Uri?,
    externalRefreshKey: Int,
    onSelectBadgeFromPreview: (Uri, Int) -> Unit,
    onDeleteBadgeFromPreview: (Uri) -> Boolean
) {
    var availableBadges by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var internalRefreshKey by remember { mutableIntStateOf(0) }

    // 只在组件首次加载或刷新键变化时获取小图标列表
    LaunchedEffect(externalRefreshKey, internalRefreshKey) {
        availableBadges = presetManager.getAvailableBadges()
    }

    if (availableBadges.isNotEmpty()) {
        Text(
            text = stringResource(R.string.click_badge_to_set),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            LazyRow(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(
                    items = availableBadges,
                    key = { it.toString() }
                ) { badgeUri ->
                    var offsetY by remember(badgeUri) { mutableFloatStateOf(0f) }
                    val density = LocalDensity.current
                    val itemHeightPx = with(density) { 64.dp.toPx() }
                    val thresholdPx = itemHeightPx * 0.5f
                    val isDeleting = offsetY > thresholdPx

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .offset { IntOffset(0, offsetY.roundToInt()) }
                            .pointerInput(badgeUri) {
                                detectVerticalDragGestures(
                                    onDragEnd = {
                                        if (offsetY > thresholdPx) {
                                            val success = onDeleteBadgeFromPreview(badgeUri)
                                            if (success) {
                                                internalRefreshKey++
                                            }
                                        }
                                        offsetY = 0f
                                    },
                                    onVerticalDrag = { change, dragAmount ->
                                        change.consume()
                                        offsetY += dragAmount
                                        if (offsetY < 0f) {
                                            offsetY = 0f
                                        }
                                        if (offsetY > itemHeightPx * 1.5f) {
                                            offsetY = itemHeightPx * 1.5f
                                        }
                                    }
                                )
                            }
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isDeleting) MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                                else Color.LightGray
                            )
                            .clickable {
                                onSelectBadgeFromPreview(badgeUri, selectedBadgeSlot)
                            }
                            .border(
                                width = 2.dp,
                                color = if (
                                    (selectedBadgeSlot == 1 && badgeUri1 == badgeUri) ||
                                    (selectedBadgeSlot == 2 && badgeUri2 == badgeUri) ||
                                    (selectedBadgeSlot == 3 && badgeUri3 == badgeUri)
                                ) Color(0xFF2196F3) else Color.Transparent,
                                shape = RoundedCornerShape(6.dp)
                            )
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(badgeUri)
                                .crossfade(true)
                                .diskCachePolicy(CachePolicy.DISABLED)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.no_badges_available),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SetBadgeDialog(
    badgeUri1: Uri?,
    badgeUri2: Uri?,
    badgeUri3: Uri?,
    presetManager: PresetManager,
    onDismiss: () -> Unit,
    onSelectBadge1: () -> Unit,
    onSelectBadge2: () -> Unit,
    onSelectBadge3: () -> Unit,
    onDeleteBadge1: () -> Unit,
    onDeleteBadge2: () -> Unit,
    onDeleteBadge3: () -> Unit,
    onAddBadge: () -> Unit,
    onSelectBadgeFromPreview: (Uri, Int) -> Unit,
    onDeleteBadgeFromPreview: (Uri) -> Boolean,
    externalRefreshKey: Int = 0 // 外部传入的刷新键，用于触发重新加载小图标列表
) {
    val context = LocalContext.current
    val extendedColors = LocalExtendedColors.current
    var selectedBadgeSlot by remember { mutableStateOf(1) } // 当前选中的小图标槽位 1-3
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = extendedColors.cardBackground,
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.set_badges_title))
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 小图标1选项
                Row(
                    modifier = Modifier
                        .wrapContentWidth()
                        .clickable { onSelectBadge1() }
                        .padding(vertical = 6.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // 左侧显示已选择的小图标或"无"
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.LightGray, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (badgeUri1 != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(badgeUri1)
                                    .crossfade(true)
                                    .diskCachePolicy(CachePolicy.DISABLED)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.none),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.badge_1),
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    // 删除按钮
                    if (badgeUri1 != null) {
                        IconButton(
                            onClick = onDeleteBadge1,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete_badge_1),
                                tint = Color.Red,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(28.dp))
                    }
                }

                // 小图标2选项
                Row(
                    modifier = Modifier
                        .wrapContentWidth()
                        .clickable { onSelectBadge2() }
                        .padding(vertical = 6.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // 左侧显示已选择的小图标或"无"
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.LightGray, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (badgeUri2 != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(badgeUri2)
                                    .crossfade(true)
                                    .diskCachePolicy(CachePolicy.DISABLED)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.none),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.badge_2),
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    // 删除按钮
                    if (badgeUri2 != null) {
                        IconButton(
                            onClick = onDeleteBadge2,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete_badge_2),
                                tint = Color.Red,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(28.dp))
                    }
                }

                // 小图标3选项
                Row(
                    modifier = Modifier
                        .wrapContentWidth()
                        .clickable { onSelectBadge3() }
                        .padding(vertical = 6.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // 左侧显示已选择的小图标或"无"
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.LightGray, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (badgeUri3 != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(badgeUri3)
                                    .crossfade(true)
                                    .diskCachePolicy(CachePolicy.DISABLED)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.none),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.badge_3),
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    // 删除按钮
                    if (badgeUri3 != null) {
                        IconButton(
                            onClick = onDeleteBadge3,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete_badge_3),
                                tint = Color.Red,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(28.dp))
                    }
                }

                // 分隔线
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // 小图标槽位选择器
                Text(
                    text = stringResource(R.string.select_badge_slot),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..3).forEach { slot ->
                        val isSelected = selectedBadgeSlot == slot
                        val hasBadge = when (slot) {
                            1 -> badgeUri1 != null
                            2 -> badgeUri2 != null
                            else -> badgeUri3 != null
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedBadgeSlot = slot },
                            label = {
                                Text(
                                    stringResource(R.string.badge_slot_label, slot, if (hasBadge) "✓" else ""),
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            },
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .weight(1f)
                        )
                    }
                }

                // 添加小图标按钮
                Button(
                    onClick = onAddBadge,
                    modifier = Modifier.padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.add_badge), fontSize = 14.sp)
                }

                // 小图标预览区域 - 使用独立的Composable以实现局部刷新
                BadgePreviewArea(
                    presetManager = presetManager,
                    selectedBadgeSlot = selectedBadgeSlot,
                    badgeUri1 = badgeUri1,
                    badgeUri2 = badgeUri2,
                    badgeUri3 = badgeUri3,
                    externalRefreshKey = externalRefreshKey,
                    onSelectBadgeFromPreview = onSelectBadgeFromPreview,
                    onDeleteBadgeFromPreview = onDeleteBadgeFromPreview
                )
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

@Composable
fun ImageViewDialog(
    imageUri: Uri,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val extendedColors = LocalExtendedColors.current
    // 使用remember(imageUri)确保当URI变化时重置bitmap状态
    var bitmap by remember(imageUri) { mutableStateOf<Bitmap?>(null) }
    
    // 加载图片获取尺寸
    LaunchedEffect(imageUri) {
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            inputStream?.use { stream ->
                bitmap = BitmapFactory.decodeStream(stream)
            }
        } catch (e: Exception) {
            AppLogger.e("加载图片失败: ${e.message}")
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = extendedColors.cardBackground,
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.view))
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(400.dp),
                    contentAlignment = Alignment.Center
                ) {
                    bitmap?.let { bmp ->
                        val aspectRatio = bmp.width.toFloat() / bmp.height.toFloat()
                        
                        // 自适应缩放到400x400区域内，保持宽高比
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } ?: run {
                        // 加载中显示占位
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                
                // 显示图片分辨率
                bitmap?.let { bmp ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${bmp.width} x ${bmp.height}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

@Composable
fun ImageCropDialog(
    imageUri: Uri,
    initialCropState: CropState = CropState(),
    onDismiss: () -> Unit,
    onCrop: (Uri, CropState) -> Unit,
    presetManager: PresetManager? = null,
    onApplyToAll: ((CropState) -> Unit)? = null
) {
    val context = LocalContext.current
    val extendedColors = LocalExtendedColors.current
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    // 从初始状态获取裁剪比例，如果没有则使用默认值
    // 只使用图片自己的裁剪设置,不使用全局SharedPreferences设置
    val savedCustomSizeWidth = if (initialCropState.customCropWidth > 0) initialCropState.customCropWidth else 0
    val savedCustomSizeHeight = if (initialCropState.customCropHeight > 0) initialCropState.customCropHeight else 0
    // 只有当自定义尺寸有效时才使用自定义裁剪模式
    val hasValidCustomSize = savedCustomSizeWidth > 0 && savedCustomSizeHeight > 0
    val savedUseCustomSize = initialCropState.useCustomCrop && hasValidCustomSize
    // 计算初始裁剪比例: 优先使用自定义尺寸计算的比例,其次使用cropRatio,默认1:1
    val initialRatio = when {
        savedUseCustomSize && hasValidCustomSize ->
            savedCustomSizeWidth.toFloat() / savedCustomSizeHeight.toFloat()
        initialCropState.cropRatio > 0 -> initialCropState.cropRatio
        else -> 1f
    }
    var selectedRatio by remember { mutableStateOf(initialRatio) } // 1f = 1:1, 0.75f = 3:4
    // 裁切框位置（从初始状态或默认居中）
    var cropPositionX by remember { mutableStateOf(initialCropState.positionX) } // 0.0f = left, 1.0f = right
    var cropPositionY by remember { mutableStateOf(initialCropState.positionY) } // 0.0f = top, 1.0f = bottom
    // 缩放比例（从初始状态或默认1.0f）
    var cropScale by remember { mutableStateOf(initialCropState.scale) }
    // 自定义裁切框大小对话框状态
    var showCustomSizeDialog by remember { mutableStateOf(false) }
    var customCropWidth by remember { mutableStateOf(if (savedCustomSizeWidth > 0) savedCustomSizeWidth.toString() else "") }
    var customCropHeight by remember { mutableStateOf(if (savedCustomSizeHeight > 0) savedCustomSizeHeight.toString() else "") }
    var useCustomSize by remember { mutableStateOf(savedUseCustomSize) }
    var customSizeWidth by remember { mutableStateOf(savedCustomSizeWidth) }
    var customSizeHeight by remember { mutableStateOf(savedCustomSizeHeight) }

    // 加载原图（带尺寸限制防止OOM）
    LaunchedEffect(imageUri) {
        try {
            // 先获取图片尺寸
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(imageUri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }
            
            // 计算采样率，限制最大尺寸为2048x2048
            val maxDimension = 2048
            var sampleSize = 1
            while (options.outWidth / sampleSize > maxDimension || 
                   options.outHeight / sampleSize > maxDimension) {
                sampleSize *= 2
            }
            
            // 重新打开流加载缩略图
            context.contentResolver.openInputStream(imageUri)?.use { stream ->
                val decodeOptions = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                }
                val loadedBitmap = BitmapFactory.decodeStream(stream, null, decodeOptions)
                bitmap = loadedBitmap
                
                if (sampleSize > 1) {
                    AppLogger.i("图片加载采样: ${options.outWidth}x${options.outHeight} -> 采样率 $sampleSize")
                }
                
                // 验证图片尺寸是否与缓存的尺寸一致
                loadedBitmap?.let { bmp ->
                    val savedImageWidth = prefs.getInt("last_crop_image_width", 0)
                    val savedImageHeight = prefs.getInt("last_crop_image_height", 0)
                    val savedCropPositionX = prefs.getFloat("crop_position_x", 0.5f)
                    val savedCropPositionY = prefs.getFloat("crop_position_y", 0.5f)
                    val savedCustomCropWidth = prefs.getInt("custom_crop_width", 0)
                    val savedCustomCropHeight = prefs.getInt("custom_crop_height", 0)
                    val savedUseCustomSize = prefs.getBoolean("use_custom_crop_size", false)
                    
                    // 如果尺寸一致，使用缓存的位置和自定义裁切框大小
                    if (bmp.width == savedImageWidth && bmp.height == savedImageHeight) {
                        cropPositionX = savedCropPositionX
                        cropPositionY = savedCropPositionY
                        // 验证自定义裁切框大小是否适合当前图片
                        if (savedUseCustomSize && 
                            savedCustomCropWidth > 0 && savedCustomCropWidth <= bmp.width &&
                            savedCustomCropHeight > 0 && savedCustomCropHeight <= bmp.height) {
                            useCustomSize = true
                            customSizeWidth = savedCustomCropWidth
                            customSizeHeight = savedCustomCropHeight
                        }
                    } else {
                        cropPositionX = 0.5f
                        cropPositionY = 0.5f
                        // 尺寸不一致，重置自定义裁切框大小
                        useCustomSize = false
                        customSizeWidth = 0
                        customSizeHeight = 0
                    }
                }
            }
        } catch (e: Exception) {
            AppLogger.e("加载图片失败: ${e.message}")
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = extendedColors.cardBackground,
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.select_crop_area))
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 预览区域 - 支持手势拖动
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    bitmap?.let { originalBitmap ->
                        val imageWidth = originalBitmap.width
                        val imageHeight = originalBitmap.height

                        // 计算裁切区域
                        val cropWidth: Int
                        val cropHeight: Int
                        if (useCustomSize) {
                            // 使用自定义裁切框大小
                            cropWidth = customSizeWidth.coerceIn(1, imageWidth)
                            cropHeight = customSizeHeight.coerceIn(1, imageHeight)
                        } else {
                            val aspectRatio = selectedRatio
                            if (imageWidth.toFloat() / imageHeight > aspectRatio) {
                                // 图片太宽，按高度计算
                                cropHeight = imageHeight
                                cropWidth = (imageHeight * aspectRatio).toInt()
                            } else {
                                // 图片太高，按宽度计算
                                cropWidth = imageWidth
                                cropHeight = (imageWidth / aspectRatio).toInt()
                            }
                        }

                        // 根据位置计算偏移
                        val maxXOffset = imageWidth - cropWidth
                        val maxYOffset = imageHeight - cropHeight
                        val xOffset = (maxXOffset * cropPositionX).toInt()
                        val yOffset = (maxYOffset * cropPositionY).toInt()

                        // 显示带裁切框的预览
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(bitmap, selectedRatio, useCustomSize, customSizeWidth, customSizeHeight) {
                                    detectDragGestures { change, _ ->
                                        change.consume()
                                        bitmap?.let { originalBitmap ->
                                            val imageWidth = originalBitmap.width
                                            val imageHeight = originalBitmap.height

                                            // 计算裁切区域尺寸
                                            val cropWidth: Int
                                            val cropHeight: Int
                                            if (useCustomSize) {
                                                cropWidth = customSizeWidth.coerceIn(1, imageWidth)
                                                cropHeight = customSizeHeight.coerceIn(1, imageHeight)
                                            } else {
                                                val aspectRatio = selectedRatio
                                                if (imageWidth.toFloat() / imageHeight > aspectRatio) {
                                                    cropHeight = imageHeight
                                                    cropWidth = (imageHeight * aspectRatio).toInt()
                                                } else {
                                                    cropWidth = imageWidth
                                                    cropHeight = (imageWidth / aspectRatio).toInt()
                                                }
                                            }

                                            val maxXOffset = (imageWidth - cropWidth).toFloat()
                                            val maxYOffset = (imageHeight - cropHeight).toFloat()

                                            // 获取手指在Canvas上的位置
                                            val touchX = change.position.x
                                            val touchY = change.position.y

                                            // 计算图片在Canvas中的实际显示区域
                                            val canvasWidth = size.width
                                            val canvasHeight = size.height
                                            val bitmapAspect = imageWidth.toFloat() / imageHeight
                                            val canvasAspect = canvasWidth.toFloat() / canvasHeight

                                            val drawWidth: Float
                                            val drawHeight: Float
                                            val drawX: Float
                                            val drawY: Float

                                            if (bitmapAspect > canvasAspect) {
                                                drawWidth = canvasWidth.toFloat()
                                                drawHeight = canvasWidth.toFloat() / bitmapAspect
                                                drawX = 0f
                                                drawY = (canvasHeight.toFloat() - drawHeight) / 2f
                                            } else {
                                                drawHeight = canvasHeight.toFloat()
                                                drawWidth = canvasHeight.toFloat() * bitmapAspect
                                                drawX = (canvasWidth.toFloat() - drawWidth) / 2f
                                                drawY = 0f
                                            }

                                            val scaleX = drawWidth / imageWidth
                                            val scaleY = drawHeight / imageHeight

                                            // 计算裁切框在Canvas中的尺寸
                                            val cropRectWidth = cropWidth * scaleX
                                            val cropRectHeight = cropHeight * scaleY

                                            // 将手指位置转换为相对于图片显示区域的坐标
                                            val relativeX = touchX - drawX
                                            val relativeY = touchY - drawY

                                            // 计算裁切框的中心位置(手指位置减去裁切框尺寸的一半)
                                            val cropCenterX = relativeX - cropRectWidth / 2
                                            val cropCenterY = relativeY - cropRectHeight / 2

                                            // 将Canvas坐标转换回图片坐标
                                            val newXOffset = (cropCenterX / scaleX).coerceIn(0f, maxXOffset)
                                            val newYOffset = (cropCenterY / scaleY).coerceIn(0f, maxYOffset)

                                            // 更新位置(归一化到0-1范围)
                                            cropPositionX = if (maxXOffset > 0) newXOffset / maxXOffset else 0.5f
                                            cropPositionY = if (maxYOffset > 0) newYOffset / maxYOffset else 0.5f
                                        }
                                    }
                                }
                        ) {
                            Image(
                                bitmap = originalBitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )

                            // 绘制裁切框和遮罩
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val canvasWidth = size.width
                                val canvasHeight = size.height

                                // 计算图片在Canvas中的实际显示区域
                                val bitmapAspect = imageWidth.toFloat() / imageHeight
                                val canvasAspect = canvasWidth / canvasHeight

                                val drawWidth: Float
                                val drawHeight: Float
                                val drawX: Float
                                val drawY: Float

                                if (bitmapAspect > canvasAspect) {
                                    drawWidth = canvasWidth
                                    drawHeight = canvasWidth / bitmapAspect
                                    drawX = 0f
                                    drawY = (canvasHeight - drawHeight) / 2f
                                } else {
                                    drawHeight = canvasHeight
                                    drawWidth = canvasHeight * bitmapAspect
                                    drawX = (canvasWidth - drawWidth) / 2f
                                    drawY = 0f
                                }

                                // 计算裁切框在Canvas中的位置
                                val scaleX = drawWidth / imageWidth
                                val scaleY = drawHeight / imageHeight

                                val cropRectLeft = drawX + xOffset * scaleX
                                val cropRectTop = drawY + yOffset * scaleY
                                val cropRectWidth = cropWidth * scaleX
                                val cropRectHeight = cropHeight * scaleY

                                // 绘制半透明遮罩（上下左右四个区域）
                                // 上
                                drawRect(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    topLeft = androidx.compose.ui.geometry.Offset(0f, 0f),
                                    size = Size(canvasWidth, cropRectTop)
                                )
                                // 下
                                drawRect(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    topLeft = androidx.compose.ui.geometry.Offset(0f, cropRectTop + cropRectHeight),
                                    size = Size(canvasWidth, canvasHeight - cropRectTop - cropRectHeight)
                                )
                                // 左
                                drawRect(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    topLeft = androidx.compose.ui.geometry.Offset(0f, cropRectTop),
                                    size = Size(cropRectLeft, cropRectHeight)
                                )
                                // 右
                                drawRect(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    topLeft = androidx.compose.ui.geometry.Offset(cropRectLeft + cropRectWidth, cropRectTop),
                                    size = Size(canvasWidth - cropRectLeft - cropRectWidth, cropRectHeight)
                                )
                                // 绘制裁切框边框
                                drawRect(
                                    color = Color.White,
                                    topLeft = androidx.compose.ui.geometry.Offset(cropRectLeft, cropRectTop),
                                    size = Size(cropRectWidth, cropRectHeight),
                                    style = Stroke(width = 3f)
                                )
                                // 绘制四角标记
                                val cornerSize = 20f
                                val corners = listOf(
                                    // 左上角
                                    Triple(cropRectLeft, cropRectTop, listOf(
                                        androidx.compose.ui.geometry.Offset(cropRectLeft, cropRectTop + cornerSize) to androidx.compose.ui.geometry.Offset(cropRectLeft, cropRectTop),
                                        androidx.compose.ui.geometry.Offset(cropRectLeft, cropRectTop) to androidx.compose.ui.geometry.Offset(cropRectLeft + cornerSize, cropRectTop)
                                    )),
                                    // 右上角
                                    Triple(cropRectLeft + cropRectWidth, cropRectTop, listOf(
                                        androidx.compose.ui.geometry.Offset(cropRectLeft + cropRectWidth - cornerSize, cropRectTop) to androidx.compose.ui.geometry.Offset(cropRectLeft + cropRectWidth, cropRectTop),
                                        androidx.compose.ui.geometry.Offset(cropRectLeft + cropRectWidth, cropRectTop) to androidx.compose.ui.geometry.Offset(cropRectLeft + cropRectWidth, cropRectTop + cornerSize)
                                    )),
                                    // 左下角
                                    Triple(cropRectLeft, cropRectTop + cropRectHeight, listOf(
                                        androidx.compose.ui.geometry.Offset(cropRectLeft, cropRectTop + cropRectHeight - cornerSize) to androidx.compose.ui.geometry.Offset(cropRectLeft, cropRectTop + cropRectHeight),
                                        androidx.compose.ui.geometry.Offset(cropRectLeft, cropRectTop + cropRectHeight) to androidx.compose.ui.geometry.Offset(cropRectLeft + cornerSize, cropRectTop + cropRectHeight)
                                    )),
                                    // 右下角
                                    Triple(cropRectLeft + cropRectWidth, cropRectTop + cropRectHeight, listOf(
                                        androidx.compose.ui.geometry.Offset(cropRectLeft + cropRectWidth - cornerSize, cropRectTop + cropRectHeight) to androidx.compose.ui.geometry.Offset(cropRectLeft + cropRectWidth, cropRectTop + cropRectHeight),
                                        androidx.compose.ui.geometry.Offset(cropRectLeft + cropRectWidth, cropRectTop + cropRectHeight - cornerSize) to androidx.compose.ui.geometry.Offset(cropRectLeft + cropRectWidth, cropRectTop + cropRectHeight)
                                    ))
                                )
                                
                                corners.forEach { (_, _, lines) ->
                                    lines.forEach { (start, end) ->
                                        drawLine(color = Color.White, start = start, end = end, strokeWidth = 3f)
                                    }
                                }
                            }
                        }

                        // 显示提示文字
                        Text(
                            text = stringResource(R.string.drag_to_move_crop),
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 显示图片分辨率
                bitmap?.let {
                    Text(
                        text = "${it.width} x ${it.height}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 比例选择
                Text(text = stringResource(R.string.crop_ratio))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterChip(
                        selected = selectedRatio == 1f && !useCustomSize,
                        onClick = {
                            selectedRatio = 1f
                            useCustomSize = false
                            customSizeWidth = 0
                            customSizeHeight = 0
                            customCropWidth = ""
                            customCropHeight = ""
                        },
                        label = { Text(stringResource(R.string.ratio_1_1)) }
                    )
                    FilterChip(
                        selected = selectedRatio == 0.75f && !useCustomSize,
                        onClick = {
                            selectedRatio = 0.75f
                            useCustomSize = false
                            customSizeWidth = 0
                            customSizeHeight = 0
                            customCropWidth = ""
                            customCropHeight = ""
                        },
                        label = { Text(stringResource(R.string.ratio_3_4)) }
                    )
                    // 自定义尺寸选项
                    FilterChip(
                        selected = useCustomSize,
                        onClick = { 
                            // 总是打开对话框，允许用户修改已设置的尺寸
                            showCustomSizeDialog = true
                            // 如果已有设置值，预填充到输入框
                            if (customSizeWidth > 0 && customSizeHeight > 0) {
                                customCropWidth = customSizeWidth.toString()
                                customCropHeight = customSizeHeight.toString()
                            }
                            useCustomSize = true
                        },
                        label = { Text(stringResource(R.string.custom)) }
                    )
                }
            }
        },
        confirmButton = {
            var isApplyingToAll by remember { mutableStateOf(false) }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 复用按钮 - 将当前裁切设置应用到所有未裁切的图片
                if (onApplyToAll != null) {
                    TextButton(
                        onClick = {
                            isApplyingToAll = true
                            // 构建当前裁切状态并传递给回调
                            val currentCropState = CropState(
                                positionX = cropPositionX,
                                positionY = cropPositionY,
                                scale = cropScale,
                                cropRatio = if (useCustomSize && customSizeWidth > 0 && customSizeHeight > 0) {
                                    customSizeWidth.toFloat() / customSizeHeight.toFloat()
                                } else {
                                    selectedRatio
                                },
                                useCustomCrop = useCustomSize,
                                customCropWidth = if (useCustomSize) customSizeWidth else 0,
                                customCropHeight = if (useCustomSize) customSizeHeight else 0
                            )
                            onApplyToAll(currentCropState)
                            isApplyingToAll = false
                        },
                        enabled = !isApplyingToAll
                    ) {
                        if (isApplyingToAll) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(stringResource(R.string.apply_to_all))
                        }
                    }
                }

                // 重置按钮 - 使用原图,不裁切
                TextButton(
                    onClick = {
                        // 返回原图URI,重置所有裁切状态
                        onCrop(imageUri, CropState(
                            positionX = 0.5f,
                            positionY = 0.5f,
                            scale = 1.0f,
                            cropRatio = 0f,
                            useCustomCrop = false,
                            customCropWidth = 0,
                            customCropHeight = 0
                        ))
                    },
                    enabled = !isApplyingToAll
                ) {
                    Text(stringResource(R.string.reset))
                }
                // 确定按钮
                TextButton(
                    onClick = {
                        bitmap?.let { originalBitmap ->
                            try {
                                val imageWidth = originalBitmap.width
                                val imageHeight = originalBitmap.height

                                // 计算裁切区域
                                val cropWidth: Int
                                val cropHeight: Int
                                if (useCustomSize) {
                                    // 使用自定义裁切框大小
                                    cropWidth = customSizeWidth.coerceIn(1, imageWidth)
                                    cropHeight = customSizeHeight.coerceIn(1, imageHeight)
                                } else {
                                    val aspectRatio = selectedRatio
                                    if (imageWidth.toFloat() / imageHeight > aspectRatio) {
                                        cropHeight = imageHeight
                                        cropWidth = (imageHeight * aspectRatio).toInt()
                                    } else {
                                        cropWidth = imageWidth
                                        cropHeight = (imageWidth / aspectRatio).toInt()
                                    }
                                }

                                // 根据位置计算偏移
                                val maxXOffset = imageWidth - cropWidth
                                val maxYOffset = imageHeight - cropHeight
                                val xOffset = (maxXOffset * cropPositionX).toInt()
                                val yOffset = (maxYOffset * cropPositionY).toInt()

                                // 执行裁切
                                val croppedBitmap = Bitmap.createBitmap(
                                    originalBitmap,
                                    xOffset,
                                    yOffset,
                                    cropWidth,
                                    cropHeight
                                )

                                // 保存裁切框位置和图片尺寸到缓存
                                prefs.edit()
                                    .putFloat("crop_position_x", cropPositionX)
                                    .putFloat("crop_position_y", cropPositionY)
                                    .putInt("last_crop_image_width", imageWidth)
                                    .putInt("last_crop_image_height", imageHeight)
                                    .apply()

                                // 保存到工作目录（如果presetManager可用）或缓存目录
                                val targetFile = if (presetManager != null) {
                                    val workDir = File(presetManager.getWorkImagesDirectory(), PresetManager.IMAGES_FOLDER_NAME)
                                    workDir.mkdirs()
                                    File(workDir, "cropped_${System.currentTimeMillis()}.webp")
                                } else {
                                    File(context.cacheDir, "cropped_${System.currentTimeMillis()}.webp")
                                }
                                FileOutputStream(targetFile).use { out ->
                                    croppedBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 100, out)
                                }
                                AppLogger.i("裁切图片已保存到: ${targetFile.absolutePath}")

                                // 计算实际的裁剪比例
                                val actualCropRatio = if (useCustomSize && customSizeWidth > 0 && customSizeHeight > 0) {
                                    customSizeWidth.toFloat() / customSizeHeight.toFloat()
                                } else {
                                    selectedRatio
                                }
                                onCrop(Uri.fromFile(targetFile), CropState(
                                    positionX = cropPositionX,
                                    positionY = cropPositionY,
                                    scale = cropScale,
                                    cropRatio = actualCropRatio,
                                    useCustomCrop = useCustomSize,
                                    customCropWidth = if (useCustomSize) customSizeWidth else 0,
                                    customCropHeight = if (useCustomSize) customSizeHeight else 0
                                ))
                            } catch (e: Exception) {
                                AppLogger.e("裁切图片失败: ${e.message}")
                                onDismiss()
                            }
                        } ?: onDismiss()
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            }
        },
    )

    // 自定义裁切框大小对话框
    if (showCustomSizeDialog) {
        // 获取图片最大尺寸
        val maxWidth = bitmap?.width ?: 9999
        val maxHeight = bitmap?.height ?: 9999
        
        AlertDialog(
            onDismissRequest = { showCustomSizeDialog = false },
            containerColor = extendedColors.cardBackground,
            title = { Text(stringResource(R.string.custom_crop_size)) },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.image_dimensions, maxWidth, maxHeight),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = customCropWidth,
                        onValueChange = { 
                            val filtered = it.filter { char -> char.isDigit() }
                            val num = filtered.toIntOrNull()
                            customCropWidth = when {
                                num == null -> ""
                                num > maxWidth -> maxWidth.toString()
                                else -> filtered
                            }
                        },
                        label = { Text(stringResource(R.string.crop_width, maxWidth)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customCropHeight,
                        onValueChange = { 
                            val filtered = it.filter { char -> char.isDigit() }
                            val num = filtered.toIntOrNull()
                            customCropHeight = when {
                                num == null -> ""
                                num > maxHeight -> maxHeight.toString()
                                else -> filtered
                            }
                        },
                        label = { Text(stringResource(R.string.crop_height, maxHeight)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val width = customCropWidth.toIntOrNull()
                        val height = customCropHeight.toIntOrNull()
                        if (width != null && height != null && width > 0 && height > 0) {
                            customSizeWidth = width.coerceIn(1, maxWidth)
                            customSizeHeight = height.coerceIn(1, maxHeight)
                            useCustomSize = true
                            // 保存到缓存
                            prefs.edit()
                                .putInt("custom_crop_width", customSizeWidth)
                                .putInt("custom_crop_height", customSizeHeight)
                                .putBoolean("use_custom_crop_size", true)
                                .apply()
                            AppLogger.i("设置自定义裁切框大小: ${customSizeWidth}x${customSizeHeight}")
                            // 重置位置到中心
                            cropPositionX = 0.5f
                            cropPositionY = 0.5f
                            showCustomSizeDialog = false
                            customCropWidth = ""
                            customCropHeight = ""
                        }
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
        )
    }
}

@Composable
fun EditImageNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    val extendedColors = LocalExtendedColors.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = extendedColors.cardBackground,
        title = { Text(stringResource(R.string.edit_image_name)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { newValue ->
                    // 根据命名规则限制输入长度
                    name = if (TextUtils.containsChinese(newValue)) {
                        // 中文名称：不超过10个字
                        newValue.take(10)
                    } else {
                        // 非中文名称：不超过18个字符
                        newValue.take(18)
                    }
                },
                label = { Text(stringResource(R.string.image_name)) },
                placeholder = { Text(currentName.ifBlank { stringResource(R.string.image_name_hint) }) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        // 为空则不添加命名(返回空字符串)
                        onConfirm(name.trim())
                    }
                )
            )
        },
        confirmButton = {},
        dismissButton = {}
    )
}

// 设置菜单项数据类
 data class SettingsItem(
     val title: String,
     val onClick: () -> Unit
 )

 // 设置菜单对话框（功能菜单）
@Composable
fun SettingsMenuDialog(
    onDismiss: () -> Unit,
    onShowInstructions: () -> Unit,
    onShowFeedback: () -> Unit,
    onImagePackage: () -> Unit,
    onShowProgramSettings: () -> Unit,
    onManagePresets: () -> Unit
) {
    val extendedColors = LocalExtendedColors.current
    val settingsItems = listOf(
         SettingsItem(
             title = stringResource(R.string.image_package),
             onClick = onImagePackage
         ),
         SettingsItem(
             title = stringResource(R.string.manage_presets),
             onClick = onManagePresets
         ),
         SettingsItem(
             title = stringResource(R.string.program_settings),
             onClick = onShowProgramSettings
         ),
         SettingsItem(
             title = stringResource(R.string.instructions),
             onClick = onShowInstructions
         ),
         SettingsItem(
             title = stringResource(R.string.about),
             onClick = onShowFeedback
         )
     )
 
     Dialog(onDismissRequest = onDismiss) {
         Card(
             modifier = Modifier
                 .fillMaxWidth(0.85f)
                 .padding(16.dp),
             shape = MaterialTheme.shapes.medium,
             colors = CardDefaults.cardColors(
                 containerColor = extendedColors.cardBackground
             )
         ) {
             Column(
                 modifier = Modifier.padding(vertical = 16.dp),
                 horizontalAlignment = Alignment.CenterHorizontally
             ) {
                 // 标题
                 Text(
                     text = stringResource(R.string.settings),
                     fontSize = 20.sp,
                     fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                     color = MaterialTheme.colorScheme.onSurface,
                     modifier = Modifier.padding(bottom = 16.dp)
                 )
                 
                 // 菜单项
                 settingsItems.forEach { item ->
                     Box(
                         modifier = Modifier
                             .fillMaxWidth()
                             .clickable { item.onClick() }
                             .padding(horizontal = 24.dp, vertical = 12.dp),
                         contentAlignment = Alignment.Center
                     ) {
                         Text(
                             text = item.title,
                             fontSize = 16.sp,
                             color = MaterialTheme.colorScheme.onSurface,
                             maxLines = 2,
                             softWrap = true,
                             textAlign = TextAlign.Center,
                             modifier = Modifier.fillMaxWidth()
                         )
                     }
                 }
             }
         }
     }
 }

// 资源管理对话框
@Composable
fun ResourceManageDialog(
    onDismiss: () -> Unit,
    presetManager: PresetManager,
    onResetTemplate: () -> Unit
) {
    val context = LocalContext.current
    val extendedColors = LocalExtendedColors.current

    // 使用资源管理器的状态
    val (state, actions) = ResourceManager.rememberResourceState(presetManager)

    // 显示清理确认对话框
    var showCleanupConfirm by remember { mutableStateOf(false) }

    // 清理确认对话框
    if (showCleanupConfirm) {
        AlertDialog(
            onDismissRequest = { showCleanupConfirm = false },
            title = {
                Text(
                    text = stringResource(R.string.confirm_cleanup),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.cleanup_data_title),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "• ${stringResource(R.string.cache_files_label)}：${ResourceManager.formatFileSize(state.details.cacheSize)}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• ${stringResource(R.string.work_images_label)}：${ResourceManager.formatFileSize(state.details.workImagesSize)}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• ${stringResource(R.string.drafts_label)}：${ResourceManager.formatFileSize(state.details.draftsSize)}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.packages_and_presets_kept),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.cleanup_warning),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                val cleanedMessage = stringResource(R.string.all_data_cleaned)
                TextButton(
                    onClick = {
                        showCleanupConfirm = false
                        actions.cleanup(
                            onResetTemplate,
                            {
                                showToastWithoutIcon(context, cleanedMessage)
                            }
                        )
                    }
                ) {
                    Text(
                        text = stringResource(R.string.confirm_cleanup),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showCleanupConfirm = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            containerColor = extendedColors.cardBackground
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = extendedColors.cardBackground
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 标题
                Text(
                    text = stringResource(R.string.resource_management),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                if (state.isCalculating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.calculating),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    // 缓存（包含缓存目录、日志文件、临时ZIP目录和孤立文件）
                    ResourceSizeItem(
                        label = stringResource(R.string.cache_files_label),
                        size = state.details.cacheSize
                    )

                    // 工作图片
                    ResourceSizeItem(
                        label = stringResource(R.string.work_images_label),
                        size = state.details.workImagesSize
                    )

                    // 草稿文件
                    ResourceSizeItem(
                        label = stringResource(R.string.drafts_label),
                        size = state.details.draftsSize
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    // 导入的图包（仅显示，不清理）
                    ResourceSizeItem(
                        label = stringResource(R.string.imported_packages),
                        size = state.details.importedPackagesSize,
                        note = stringResource(R.string.kept_note)
                    )

                    // 预设文件（仅显示，不清理）
                    ResourceSizeItem(
                        label = stringResource(R.string.preset_files),
                        size = state.details.presetsSize,
                        note = stringResource(R.string.kept_note)
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    // 可清理总计（不含导入的图包和预设文件）
                    val cleanableSize = state.details.cacheSize +
                            state.details.workImagesSize +
                            state.details.draftsSize
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.cleanable_total),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = ResourceManager.formatFileSize(cleanableSize),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 清理按钮
                val cleanableSize = state.details.cacheSize +
                        state.details.workImagesSize +
                        state.details.draftsSize
                Button(
                    onClick = { showCleanupConfirm = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isCleaning && !state.isCalculating && cleanableSize > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = Color.White
                    )
                ) {
                    if (state.isCleaning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.cleanup_button),
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 关闭按钮
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

// 资源大小项
@Composable
private fun ResourceSizeItem(
    label: String,
    size: Long,
    note: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (note != null) {
                Text(
                    text = note,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
        Text(
            text = ResourceManager.formatFileSize(size),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (size > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// 使用说明对话框
@Composable
fun InstructionsDialog(
    onDismiss: () -> Unit
) {
    val extendedColors = LocalExtendedColors.current
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = extendedColors.cardBackground,
        title = { Text(stringResource(R.string.instructions)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState())
            ) {
                Text(
                    text = stringResource(R.string.instructions_content),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

// 管理图包对话框
@Composable
fun ManagePackagesDialog(
    context: android.content.Context,
    presetManager: PresetManager,
    onDismiss: () -> Unit,
    onImportPackage: () -> Unit,
    onPackageSelected: (PackageItem.Imported) -> Unit,
    onExportPackage: (PackageItem.Imported) -> Unit
) {
    val extendedColors = LocalExtendedColors.current
    var importedPackages by remember { mutableStateOf(presetManager.getImportedPackages()) }
    var showDeleteConfirm by remember { mutableStateOf<PackageItem.Imported?>(null) }
    val scope = rememberCoroutineScope()

    // 刷新导入的图包列表
    fun refreshImportedPackages() {
        importedPackages = presetManager.getImportedPackages()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = extendedColors.cardBackground
            )
        ) {
            Column(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 标题
                Text(
                    text = stringResource(R.string.manage_packages_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 导入图包按钮
                Button(
                    onClick = {
                        onImportPackage()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = extendedColors.buttonContainer,
                        contentColor = extendedColors.buttonContent
                    )
                ) {
                    Text(stringResource(R.string.import_package_short))
                }

                // 已导入图包区域
                if (importedPackages.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.imported_packages),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 150.dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        items(importedPackages) { packageFile ->
                            val packageItem = PackageItem.Imported(
                                name = packageFile.nameWithoutExtension,
                                file = packageFile
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = packageItem.name,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    // 导出按钮
                                    TextButton(
                                        onClick = { onExportPackage(packageItem) }
                                    ) {
                                        Text(stringResource(R.string.export_package))
                                    }
                                    TextButton(
                                        onClick = { onPackageSelected(packageItem) }
                                    ) {
                                        Text(stringResource(R.string.import_package_short))
                                    }
                                    TextButton(
                                        onClick = { showDeleteConfirm = packageItem }
                                    ) {
                                        Text(
                                            stringResource(R.string.delete_package),
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // 删除确认对话框
    showDeleteConfirm?.let { packageToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            containerColor = extendedColors.cardBackground,
            title = { Text(stringResource(R.string.confirm_delete)) },
            text = { Text(stringResource(R.string.confirm_delete_message, packageToDelete.name)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            presetManager.deleteImportedPackage(packageToDelete.file)
                            refreshImportedPackages()
                            showDeleteConfirm = null
                        }
                    }
                ) {
                    Text(
                        stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

// 语言选择对话框
@Composable
fun LanguageSelectionDialog(
    currentLanguage: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    // 语言列表 - 使用各自语言的本地名称,不随系统语言变化
    val languages = listOf(
        "zh" to "中文",
        "en" to "English",
        "ja" to "日本語",
        "ko" to "한국어",
        "ru" to "Русский",
        "de" to "Deutsch",
        "fr" to "Français",
        "es" to "Español",
        "ar" to "العربية",
        "pt" to "Português"
    )
    val extendedColors = LocalExtendedColors.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = extendedColors.cardBackground
            )
        ) {
            Column(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 标题
                Text(
                    text = stringResource(R.string.select_language),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 语言列表
                languages.forEach { (code, name) ->
                    val isSelected = code == currentLanguage
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLanguageSelected(code)
                                onDismiss()
                            }
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // 选中项使用圆角背景高亮,文字使用主题色
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else {
                            Text(
                                text = name,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

 // 图包确认对话框
@Composable
fun PackageConfirmDialog(
    packageName: String,
    imageCount: Int,
    isImporting: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (ImportTarget) -> Unit
) {
    val extendedColors = LocalExtendedColors.current

    if (isImporting) {
        // 导入中状态显示
        AlertDialog(
            onDismissRequest = { },
            containerColor = extendedColors.cardBackground,
            title = { Text(stringResource(R.string.importing)) },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(stringResource(R.string.importing), fontSize = 14.sp)
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    } else {
        // 直接显示导入目标选择对话框 - 使用与ImportTargetDialog一致的风格
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = extendedColors.cardBackground,
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.select_import_target))
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.import_target_description),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { onConfirm(ImportTarget.PENDING) },
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = extendedColors.buttonContainer,
                                contentColor = extendedColors.buttonContent
                            )
                        ) {
                            Text(stringResource(R.string.import_to_pending))
                        }
                        Button(
                            onClick = { onConfirm(ImportTarget.BADGES) },
                            modifier = Modifier.weight(1f).padding(start = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = extendedColors.buttonContainer,
                                contentColor = extendedColors.buttonContent
                            )
                        ) {
                            Text(stringResource(R.string.import_to_badges))
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

// 导入图包对话框
@Composable
fun ImportPackageDialog(
    context: android.content.Context,
    presetManager: PresetManager,
    pendingImages: List<Uri>,
    onPendingImagesChanged: (List<Uri>) -> Unit,
    onDismiss: () -> Unit,
    onSkipDraftSave: (() -> Unit)? = null,
    onResumeDraftSave: (() -> Unit)? = null
) {
    val extendedColors = LocalExtendedColors.current
    var isImporting by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showTargetDialog by remember { mutableStateOf(false) }
    var pendingZipUri by remember { mutableStateOf<Uri?>(null) }
    var pendingZipFileName by remember { mutableStateOf<String>("") }
    var pendingPassword by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    
    // ZIP文件选择器
    val zipPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { zipUri ->
            pendingZipUri = zipUri
            pendingPassword = null
            // 获取原始文件名
            pendingZipFileName = ImageResourceManager.getFileNameFromUri(zipUri) ?: "imported_${System.currentTimeMillis()}.zip"
            // 先检测ZIP是否加密，然后显示导入目标选择对话框
            scope.launch {
                isImporting = true
                try {
                    // 检测ZIP是否加密
                    val tempDir = context.cacheDir.resolve("zip_check_${System.currentTimeMillis()}")
                    tempDir.mkdirs()
                    val tempZipFile = File(tempDir, "temp.zip")
                    context.contentResolver.openInputStream(zipUri)?.use { input ->
                        tempZipFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    val zipFile = net.lingala.zip4j.ZipFile(tempZipFile)
                    val isEncrypted = zipFile.isEncrypted
                    tempDir.deleteRecursively()
                    
                    isImporting = false
                    if (isEncrypted) {
                        // ZIP已加密，需要密码，显示密码对话框
                        showPasswordDialog = true
                        AppLogger.i("ZIP需要密码，显示密码输入对话框")
                    } else {
                        // 未加密，直接显示导入目标选择对话框
                        showTargetDialog = true
                    }
                } catch (e: Exception) {
                    isImporting = false
                    showToastWithoutIcon(
                        context,
                        context.getString(R.string.import_failed, e.message),
                        Toast.LENGTH_LONG
                    )
                    onDismiss()
                }
            }
        } ?: run {
            // 用户取消了选择，恢复草稿保存
            onResumeDraftSave?.invoke()
        }
    }

    // 处理带密码的ZIP导入确认
    fun processZipWithPassword(password: String) {
        pendingPassword = password
        showPasswordDialog = false
        showTargetDialog = true
    }
    
    // 处理导入目标选择
    fun handleImportTargetSelected(target: ImportTarget) {
        // 先显示导入中，然后解压并保存
        scope.launch {
            isImporting = true
            showTargetDialog = false
            
            try {
                pendingZipUri?.let { uri ->
                    // 确定目标目录
                    val targetDir = when (target) {
                        ImportTarget.PENDING -> File(presetManager.getWorkImagesDirectory(), PresetManager.IMAGES_FOLDER_NAME)
                        ImportTarget.BADGES -> File(presetManager.getWorkImagesDirectory(), PresetManager.BADGES_FOLDER_NAME)
                    }
                    
                    // 解压ZIP文件到目标目录
                    val imageUris = ResourcePackageManager.importImagesFromZip(context, uri, targetDir, pendingPassword)
                    if (imageUris.isNotEmpty()) {
                        // 保存图包到图包目录，使用原始文件名
                        val savedPackageFile = presetManager.saveImportedPackage(uri, pendingZipFileName)
                        if (savedPackageFile != null) {
                            AppLogger.i("图包已保存到图包目录: ${savedPackageFile.absolutePath}")
                        }
                        
                        isImporting = false
                        // 直接更新UI，不需要再复制
                        when (target) {
                            ImportTarget.PENDING -> {
                                onPendingImagesChanged(pendingImages + imageUris)
                                AppLogger.i("导入ZIP到待分级区域: ${imageUris.size} 张图片")
                                showToastWithoutIcon(
                                    context,
                                    context.getString(R.string.import_success, imageUris.size)
                                )
                            }
                            ImportTarget.BADGES -> {
                                AppLogger.i("导入ZIP到小图标区域: ${imageUris.size} 个")
                                showToastWithoutIcon(
                                    context,
                                    context.getString(R.string.badge_added)
                                )
                            }
                        }
                        onDismiss()
                    } else {
                        isImporting = false
                        showToastWithoutIcon(
                            context,
                            context.getString(R.string.no_images_in_zip)
                        )
                        onDismiss()
                    }
                }
            } catch (e: Exception) {
                isImporting = false
                showToastWithoutIcon(
                    context,
                    context.getString(R.string.import_failed, e.message),
                    Toast.LENGTH_LONG
                )
                onDismiss()
            } finally {
                // 恢复草稿保存
                onResumeDraftSave?.invoke()
            }
        }
    }

    AlertDialog(
        onDismissRequest = { if (!isImporting) onDismiss() },
        containerColor = extendedColors.cardBackground,
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.import_image_package))
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isImporting) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.importing))
                } else {
                    Text(
                        text = stringResource(R.string.import_package_description),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            onSkipDraftSave?.invoke()
                            zipPicker.launch("application/zip")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = extendedColors.buttonContainer,
                            contentColor = extendedColors.buttonContent
                        )
                    ) {
                        Text(stringResource(R.string.select_zip_file))
                    }
                }
            }
        },
        confirmButton = {}
    )
    
    // 密码输入对话框（仅在有密码保护时显示）
    if (showPasswordDialog) {
        ZipPasswordDialog(
            showError = passwordError,
            onDismiss = { 
                showPasswordDialog = false
                pendingZipUri = null
                onDismiss()
            },
            onConfirm = { password ->
                if (password.isNotBlank()) {
                    processZipWithPassword(password)
                }
            }
        )
    }
    
    // 导入位置选择对话框
    if (showTargetDialog) {
        ImportTargetDialog(
            onDismiss = {
                showTargetDialog = false
                pendingZipUri = null
                pendingPassword = null
                onDismiss()
            },
            onTargetSelected = { target ->
                handleImportTargetSelected(target)
            }
        )
    }
}

// ZIP密码输入对话框
@Composable
fun ZipPasswordDialog(
    showError: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var password by remember { mutableStateOf("") }
    val extendedColors = LocalExtendedColors.current
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = extendedColors.cardBackground,
        title = { Text(stringResource(R.string.enter_zip_password)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.zip_password_hint),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(R.string.password)) },
                    singleLine = true,
                    isError = showError
                )
                if (showError) {
                    Text(
                        text = stringResource(R.string.wrong_password),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { if (password.isNotBlank()) onConfirm(password) }) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

// 导入位置选择对话框
@Composable
fun ImportTargetDialog(
    onDismiss: () -> Unit,
    onTargetSelected: (ImportTarget) -> Unit
) {
    val extendedColors = LocalExtendedColors.current
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = extendedColors.cardBackground,
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.select_import_target))
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.import_target_description),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { onTargetSelected(ImportTarget.PENDING) },
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = extendedColors.buttonContainer,
                            contentColor = extendedColors.buttonContent
                        )
                    ) {
                        Text(stringResource(R.string.import_to_pending))
                    }
                    Button(
                        onClick = { onTargetSelected(ImportTarget.BADGES) },
                        modifier = Modifier.weight(1f).padding(start = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = extendedColors.buttonContainer,
                            contentColor = extendedColors.buttonContent
                        )
                    ) {
                        Text(stringResource(R.string.import_to_badges))
                    }
                }
            }
        },
        confirmButton = {}
    )
}

// 保存Bitmap到相册
suspend fun saveBitmapToGallery(
    context: android.content.Context,
    bitmap: Bitmap,
    title: String = ""
) {
    AppLogger.i("开始保存图片到相册")
    try {
        withContext(Dispatchers.IO) {
            // 生成文件名：标题_梯度表_时间戳.webp（确保唯一性）
            val tierListSuffix = context.getString(R.string.tier_list_suffix)
            val sanitizedTitle = if (title.isNotBlank()) {
                title.replace(Regex("[^a-zA-Z0-9\\u4e00-\\u9fa5_-]"), "_").take(50)
            } else {
                "tier_list"
            }
            val timeStamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date())
            val fileName = "${sanitizedTitle}_${tierListSuffix}_${timeStamp}.webp"

            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/webp")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            uri?.let {
                val outputStream: OutputStream? = context.contentResolver.openOutputStream(it)
                outputStream?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 100, stream)
                }
                withContext(Dispatchers.Main) {
                    showToastWithoutIcon(context, context.getString(R.string.saved_to_gallery))
                    AppLogger.i("图片保存成功: $it")
                }
            }
        }
    } catch (e: Exception) {
        AppLogger.e("图片保存失败", e)
        showToastWithoutIcon(context, context.getString(R.string.save_failed, e.message))
    }
}

// 分享Bitmap图片
suspend fun shareBitmap(
    context: android.content.Context,
    bitmap: Bitmap,
    title: String = ""
) {
    AppLogger.i("开始分享图片")
    try {
        withContext(Dispatchers.IO) {
            val cacheDir = context.cacheDir

            // 清理之前分享生成的所有图片
            cacheDir.listFiles { file ->
                file.name.endsWith("_${context.getString(R.string.tier_list_suffix)}.webp")
            }?.forEach { file ->
                file.delete()
                AppLogger.d("清理旧分享文件: ${file.name}")
            }

            // 生成文件名：标题_梯度表.webp
            val tierListSuffix = context.getString(R.string.tier_list_suffix)
            val sanitizedTitle = if (title.isNotBlank()) {
                title.replace(Regex("[^a-zA-Z0-9\\u4e00-\\u9fa5_-]"), "_").take(50)
            } else {
                "tier_list"
            }
            val fileName = "${sanitizedTitle}_${tierListSuffix}.webp"

            // 创建临时文件
            val file = File(cacheDir, fileName)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 100, out)
            }

            // 获取FileProvider Uri
            val uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            // 创建分享Intent
            val shareIntent = android.content.Intent().apply {
                action = android.content.Intent.ACTION_SEND
                type = "image/webp"
                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                putExtra(android.content.Intent.EXTRA_SUBJECT, context.getString(R.string.share_image))
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // 启动分享对话框
            val chooser = android.content.Intent.createChooser(shareIntent, context.getString(R.string.share_image))
            withContext(Dispatchers.Main) {
                context.startActivity(chooser)
                AppLogger.i("分享对话框已启动")
            }
        }
    } catch (e: Exception) {
        AppLogger.e("分享图片失败", e)
        withContext(Dispatchers.Main) {
            showToastWithoutIcon(context, context.getString(R.string.share_failed, e.message))
        }
    }
}

// 保存梯度表图片
suspend fun saveTierListImage(
    context: android.content.Context,
    tiers: List<TierItem>,
    tierImages: List<TierImage>,
    title: String = context.getString(R.string.tier_list_default_title),
    authorName: String = "",
    externalBadgeEnabled: Boolean = false,
    disableCustomFont: Boolean = false,
    nameBelowImage: Boolean = false
) {
    AppLogger.i("开始保存梯度表图片")
    AppLogger.i("标题: $title, 作者: $authorName, 层级数: ${tiers.size}, 图片数: ${tierImages.size}")
    try {
        // 使用 generateTierListBitmap 生成图片（浅色主题）
        val bitmap = generateTierListBitmap(
            context = context,
            tiers = tiers,
            tierImages = tierImages,
            title = title,
            authorName = authorName,
            isDarkTheme = false,
            externalBadgeEnabled = externalBadgeEnabled,
            disableCustomFont = disableCustomFont,
            nameBelowImage = nameBelowImage
        )

        withContext(Dispatchers.IO) {
            // 保存到相册
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "梯度表_${System.currentTimeMillis()}.webp")
                put(MediaStore.Images.Media.MIME_TYPE, "image/webp")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            uri?.let {
                val outputStream: OutputStream? = context.contentResolver.openOutputStream(it)
                outputStream?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 85, stream)
                }
                withContext(Dispatchers.Main) {
                    showToastWithoutIcon(context, context.getString(R.string.saved_to_gallery))
                    AppLogger.i("梯度表图片保存成功: $it")
                }
            }
        }
    } catch (e: Exception) {
        AppLogger.e("梯度表图片保存失败", e)
        showToastWithoutIcon(context, context.getString(R.string.save_failed, e.message))
    }
}

// 数据类

/**
 * 裁剪状态数据类
 */
data class CropState(
    val positionX: Float = 0.5f,
    val positionY: Float = 0.5f,
    val scale: Float = 1.0f,
    val cropRatio: Float = 1f,  // 裁剪比例: 1f = 1:1, 0.75f = 3:4, 1.33f = 4:3
    val useCustomCrop: Boolean = false,
    val customCropWidth: Int = 0,
    val customCropHeight: Int = 0
)

// 预设操作类型
enum class PresetOperation {
    EXPORT, SAVE
}


