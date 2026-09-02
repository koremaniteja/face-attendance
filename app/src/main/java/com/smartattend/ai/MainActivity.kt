package com.smartattend.ai

import android.Manifest
import android.content.pm.PackageManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.Executors

private val Navy = Color(0xFF102A43)
private val Blue = Color(0xFF1769E0)
private val PaleBlue = Color(0xFFEAF2FF)
private val Green = Color(0xFF16865B)
private val PaleGreen = Color(0xFFE8F7F0)
private val Orange = Color(0xFFB96800)
private val PaleOrange = Color(0xFFFFF2DE)

data class Student(val name: String, val roll: String, val present: Boolean = false)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SmartAttendApp() }
    }
}

@Composable
private fun SmartAttendApp() {
    var signedIn by remember { mutableStateOf(false) }
    Surface(color = Color(0xFFF7F9FC), modifier = Modifier.fillMaxSize()) {
        if (signedIn) AttendanceApp(onSignOut = { signedIn = false }) else LoginScreen { signedIn = true }
    }
}

@Composable
private fun LoginScreen(onLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(76.dp).clip(RoundedCornerShape(24.dp)).background(Blue),
            contentAlignment = Alignment.Center
        ) { Icon(Icons.Default.Face, null, tint = Color.White, modifier = Modifier.size(42.dp)) }
        Spacer(Modifier.height(20.dp))
        Text("SmartAttend", color = Navy, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Text("AI-powered attendance, made simple", color = Color(0xFF627D98), fontSize = 15.sp)
        Spacer(Modifier.height(42.dp))
        OutlinedTextField(email, { email = it }, label = { Text("Email address") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(14.dp))
        OutlinedTextField(
            password, { password = it }, label = { Text("Password") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(), leadingIcon = { Icon(Icons.Default.Lock, null) },
            modifier = Modifier.fillMaxWidth()
        )
        TextButton(onClick = {}, modifier = Modifier.align(Alignment.End)) { Text("Forgot password?") }
        Button(
            onClick = onLogin, modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Blue)
        ) { Text("Sign in", fontSize = 16.sp, fontWeight = FontWeight.SemiBold) }
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("New to SmartAttend?", color = Color(0xFF627D98))
            TextButton(onClick = onLogin) { Text("Create account") }
        }
        Text("Demo mode • Firebase can be connected from project settings", color = Color(0xFF829AB1), fontSize = 11.sp)
    }
}

private enum class Tab(val label: String) {
    Home("Home"), Classes("Classes"), Students("Students"), Reports("Reports")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttendanceApp(onSignOut: () -> Unit) {
    var tab by remember { mutableStateOf(Tab.Home) }
    var showScanner by remember { mutableStateOf(false) }
    var showAddClass by remember { mutableStateOf(false) }
    var showSchedule by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var showExport by remember { mutableStateOf(false) }
    val students = remember {
        mutableStateListOf(
            Student("Aarav Sharma", "CS-001", true), Student("Ishita Patel", "CS-002", true),
            Student("Rohan Mehta", "CS-003"), Student("Meera Nair", "CS-004", true),
            Student("Kabir Singh", "CS-005"), Student("Ananya Rao", "CS-006", true)
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SmartAttend", color = Navy, fontWeight = FontWeight.Bold) },
                actions = { IconButton(onClick = onSignOut) { Icon(Icons.Default.Person, "Profile", tint = Blue) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF7F9FC))
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                Tab.values().forEach { item ->
                    NavigationBarItem(selected = tab == item, onClick = { tab = item },
                        icon = { Icon(when (item) { Tab.Home -> Icons.Default.Home; Tab.Classes -> Icons.Default.Class; Tab.Students -> Icons.Default.Groups; Tab.Reports -> Icons.Default.Analytics }, item.label) },
                        label = { Text(item.label) })
                }
            }
        },
        floatingActionButton = {
            if (tab == Tab.Home) FloatingActionButton(onClick = { showScanner = true }, containerColor = Blue, contentColor = Color.White) {
                Icon(Icons.Default.QrCodeScanner, "Start face scan")
            }
        }
    ) { padding ->
        when (tab) {
            Tab.Home -> HomeScreen(
                padding,
                students,
                onScan = { showScanner = true },
                onSchedule = { showSchedule = true },
                onHistory = { showHistory = true }
            )
            Tab.Classes -> ClassesScreen(padding, onAdd = { showAddClass = true })
            Tab.Students -> StudentsScreen(padding, students)
            Tab.Reports -> ReportsScreen(padding, students, onExport = { showExport = true })
        }
    }
    if (showScanner) ScannerDialog(students) { showScanner = false }
    if (showAddClass) AddClassDialog { showAddClass = false }
    if (showSchedule) ScheduleDialog { showSchedule = false }
    if (showHistory) HistoryDialog { showHistory = false }
    if (showExport) ExportDialog(students) { showExport = false }
}

@Composable
private fun HomeScreen(
    padding: PaddingValues,
    students: List<Student>,
    onScan: () -> Unit,
    onSchedule: () -> Unit,
    onHistory: () -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = padding.calculateTopPadding() + 8.dp, bottom = 88.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Good morning, Professor", color = Color(0xFF627D98), fontSize = 14.sp)
            Text("Ready to take attendance?", color = Navy, fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Blue), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("AI FACE SCANNER", color = Color(0xFFBBD4FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp)); Text("Mark attendance in seconds", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(14.dp)); Button(onClick = onScan, colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Blue)) { Text("Start scanning") }
                    }
                    Icon(Icons.Default.Face, null, tint = Color(0x667BAAFF), modifier = Modifier.size(88.dp))
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Present", "${students.count { it.present }}", "Today", PaleGreen, Green, Modifier.weight(1f))
                StatCard("Attendance", "86%", "This week", PaleBlue, Blue, Modifier.weight(1f))
                StatCard("Classes", "04", "Active", PaleOrange, Orange, Modifier.weight(1f))
            }
        }
        item { SectionTitle("Today’s schedule", "View all", onSchedule) }
        item { ScheduleCard("Data Structures", "CS - Semester 4", "09:00 AM", "Room 204", true, onClick = onSchedule) }
        item { ScheduleCard("Machine Learning", "AI - Semester 6", "11:30 AM", "Lab 3", false) }
        item { SectionTitle("Recent activity", "See history", onHistory) }
        item { ActivityRow("Attendance marked", "Data Structures • Just now", Icons.Default.CheckCircle, Green) }
        item { ActivityRow("New student enrolled", "Rohan Mehta • Yesterday", Icons.Default.Person, Blue) }
    }
}

@Composable
private fun StatCard(title: String, value: String, subtitle: String, bg: Color, tint: Color, modifier: Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = bg), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(12.dp)) { Text(title, color = tint, fontSize = 11.sp); Text(value, color = Navy, fontSize = 22.sp, fontWeight = FontWeight.Bold); Text(subtitle, color = Color(0xFF627D98), fontSize = 10.sp) }
    }
}

@Composable
private fun SectionTitle(title: String, action: String, onAction: () -> Unit = {}) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = Navy, fontSize = 18.sp, fontWeight = FontWeight.Bold); TextButton(onClick = onAction) { Text(action, color = Blue) }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ScheduleCard(name: String, course: String, time: String, room: String, active: Boolean, onClick: () -> Unit = {}) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(45.dp).clip(CircleShape).background(if (active) PaleBlue else PaleOrange), contentAlignment = Alignment.Center) { Icon(Icons.Default.MenuBook, null, tint = if (active) Blue else Orange) }
            Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(name, color = Navy, fontWeight = FontWeight.Bold); Text(course, color = Color(0xFF627D98), fontSize = 12.sp); Spacer(Modifier.height(4.dp)); Text("$time  •  $room", color = Color(0xFF829AB1), fontSize = 12.sp) }
            Icon(Icons.Default.MoreVert, null, tint = Color(0xFF829AB1))
        }
    }
}

@Composable
private fun ActivityRow(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(38.dp).clip(CircleShape).background(tint.copy(alpha = .12f)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp)) }
        Spacer(Modifier.width(12.dp)); Column { Text(title, color = Navy, fontWeight = FontWeight.SemiBold); Text(subtitle, color = Color(0xFF829AB1), fontSize = 12.sp) }
    }
}

@Composable
private fun ClassesScreen(padding: PaddingValues, onAdd: () -> Unit) {
    val classes = listOf("Data Structures" to "CS - Semester 4 • 42 students", "Machine Learning" to "AI - Semester 6 • 36 students", "Computer Networks" to "CS - Semester 4 • 40 students")
    LazyColumn(contentPadding = PaddingValues(20.dp, padding.calculateTopPadding() + 8.dp, 20.dp, 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Your classes", color = Navy, fontSize = 25.sp, fontWeight = FontWeight.Bold); Text("Manage subjects and attendance", color = Color(0xFF627D98)) }
        item { Spacer(Modifier.height(4.dp)); OutlinedButton(onClick = onAdd, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("Create new class") } }
        items(classes) { (name, detail) -> Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Class, null, tint = Blue, modifier = Modifier.size(30.dp)); Spacer(Modifier.width(14.dp)); Column(Modifier.weight(1f)) { Text(name, color = Navy, fontWeight = FontWeight.Bold); Text(detail, color = Color(0xFF627D98), fontSize = 12.sp) }; IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null) } } } }
    }
}

@Composable
private fun StudentsScreen(padding: PaddingValues, students: List<Student>) {
    LazyColumn(contentPadding = PaddingValues(20.dp, padding.calculateTopPadding() + 8.dp, 20.dp, 24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Students", color = Navy, fontSize = 25.sp, fontWeight = FontWeight.Bold); Text("${students.size} enrolled in Data Structures", color = Color(0xFF627D98)); Spacer(Modifier.height(8.dp)) }
        items(students) { student -> Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(14.dp)) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(42.dp).clip(CircleShape).background(PaleBlue), contentAlignment = Alignment.Center) { Text(student.name.take(1), color = Blue, fontWeight = FontWeight.Bold) }; Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(student.name, color = Navy, fontWeight = FontWeight.SemiBold); Text(student.roll, color = Color(0xFF829AB1), fontSize = 12.sp) }; Text(if (student.present) "Present" else "Absent", color = if (student.present) Green else Orange, fontSize = 12.sp, fontWeight = FontWeight.Bold) } } }
    }
}

@Composable
private fun ReportsScreen(padding: PaddingValues, students: List<Student>, onExport: () -> Unit) {
    LazyColumn(contentPadding = PaddingValues(20.dp, padding.calculateTopPadding() + 8.dp, 20.dp, 24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Text("Reports", color = Navy, fontSize = 25.sp, fontWeight = FontWeight.Bold); Text("Track attendance trends", color = Color(0xFF627D98)) }
        item { Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) { Column(Modifier.padding(20.dp)) { Text("Overall attendance", color = Color(0xFF627D98)); Text("86.4%", color = Navy, fontSize = 34.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(14.dp)); Box(Modifier.fillMaxWidth().height(10.dp).clip(CircleShape).background(PaleBlue)) { Box(Modifier.fillMaxWidth(.864f).height(10.dp).clip(CircleShape).background(Blue)) }; Spacer(Modifier.height(8.dp)); Text("＋4.2% compared with last month", color = Green, fontSize = 12.sp) } } }
        item { SectionTitle("Class attendance", "Export CSV", onExport) }
        item { ReportRow("Data Structures", "86%", students.count { it.present }, students.size) }
        item { ReportRow("Machine Learning", "91%", 33, 36) }
        item { ReportRow("Computer Networks", "82%", 33, 40) }
    }
}

@Composable
private fun ReportRow(name: String, percent: String, present: Int, total: Int) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(14.dp)) { Column(Modifier.padding(16.dp)) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(name, color = Navy, fontWeight = FontWeight.SemiBold); Text(percent, color = Blue, fontWeight = FontWeight.Bold) }; Spacer(Modifier.height(10.dp)); Box(Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).background(PaleBlue)) { Box(Modifier.fillMaxWidth(present.toFloat() / total).height(8.dp).clip(CircleShape).background(Blue)) }; Spacer(Modifier.height(6.dp)); Text("$present of $total students present today", color = Color(0xFF829AB1), fontSize = 12.sp) } }
}

@Composable
private fun ScannerDialog(students: MutableList<Student>, onDismiss: () -> Unit) {
    var scanned by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var cameraGranted by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        cameraGranted = it
    }
    LaunchedEffect(Unit) {
        if (!cameraGranted) permissionLauncher.launch(Manifest.permission.CAMERA)
    }
    AlertDialog(onDismissRequest = onDismiss, confirmButton = {
        Button(onClick = {
            if (scanned) onDismiss() else {
                if (!cameraGranted) permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }, enabled = scanned || !cameraGranted, colors = ButtonDefaults.buttonColors(containerColor = Blue)) {
            Text(if (scanned) "Done" else "Allow camera")
        }
    }, dismissButton = { TextButton(onClick = onDismiss) { Text("Close") } }, title = { Text("AI face scanner", color = Navy) }, text = {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (cameraGranted && !scanned) {
                FaceCameraPreview(
                    modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(18.dp)),
                    onFaceDetected = {
                        students[0] = students[0].copy(present = true)
                        scanned = true
                    }
                )
            } else {
                Box(Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(18.dp)).background(Navy), contentAlignment = Alignment.Center) {
                    Icon(if (scanned) Icons.Default.CheckCircle else Icons.Default.Face, null, tint = if (scanned) Color(0xFF76E3B5) else Color.White, modifier = Modifier.size(75.dp))
                }
            }
            Spacer(Modifier.height(14.dp)); Text(if (scanned) "Aarav Sharma recognized" else "Position a student’s face inside the frame", color = Navy, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(4.dp)); Text(if (scanned) "Attendance marked successfully for Data Structures." else "The AI scanner checks enrolled students securely.", color = Color(0xFF627D98), fontSize = 13.sp)
        }
    })
}

@Composable
private fun FaceCameraPreview(modifier: Modifier, onFaceDetected: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var faceReported by remember { mutableStateOf(false) }
    val detector = remember {
        FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .build()
        )
    }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            detector.close()
            cameraExecutor.shutdown()
        }
    }
    AndroidView(
        modifier = modifier,
        factory = { viewContext ->
            val previewView = PreviewView(viewContext)
            val providerFuture = ProcessCameraProvider.getInstance(context)
            providerFuture.addListener({
                val provider = providerFuture.get()
                val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    analyzeFace(imageProxy, detector) {
                        if (!faceReported) {
                            faceReported = true
                            onFaceDetected()
                        }
                    }
                }
                provider.unbindAll()
                provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_FRONT_CAMERA, preview, analysis)
            }, ContextCompat.getMainExecutor(context))
            previewView.scaleType = PreviewView.ScaleType.FILL_CENTER
            previewView
        }
    )
}

private fun analyzeFace(imageProxy: ImageProxy, detector: com.google.mlkit.vision.face.FaceDetector, onFaceDetected: () -> Unit) {
    val mediaImage = imageProxy.image
    if (mediaImage == null) {
        imageProxy.close()
        return
    }
    detector.process(InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees))
        .addOnSuccessListener { faces ->
            if (faces.isNotEmpty()) onFaceDetected()
        }
        .addOnCompleteListener { imageProxy.close() }
}

@Composable
private fun AddClassDialog(onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Blue)
            ) { Text("Create class") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("Create new class", color = Navy) },
        text = { OutlinedTextField(name, { name = it }, label = { Text("Class name") }, singleLine = true) }
    )
}

@Composable
private fun ScheduleDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Blue)) { Text("Close") } },
        title = { Text("Today’s schedule", color = Navy) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ScheduleDetail("09:00 AM", "Data Structures", "CS - Semester 4", "Room 204", true)
                ScheduleDetail("11:30 AM", "Machine Learning", "AI - Semester 6", "Lab 3", false)
                ScheduleDetail("02:00 PM", "Computer Networks", "CS - Semester 4", "Room 108", false)
            }
        }
    )
}

@Composable
private fun ScheduleDetail(time: String, name: String, course: String, room: String, active: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(10.dp).clip(CircleShape).background(if (active) Green else Color(0xFFCBD5E1))
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(name, color = Navy, fontWeight = FontWeight.SemiBold)
            Text("$time  •  $room", color = Color(0xFF627D98), fontSize = 12.sp)
            Text(course, color = Color(0xFF829AB1), fontSize = 11.sp)
        }
        if (active) Text("Now", color = Green, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun HistoryDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Blue)) { Text("Done") } },
        title = { Text("Recent activity", color = Navy) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                ActivityRow("Attendance marked", "Data Structures • Just now", Icons.Default.CheckCircle, Green)
                ActivityRow("New student enrolled", "Rohan Mehta • Yesterday", Icons.Default.Person, Blue)
                ActivityRow("Class created", "Computer Networks • Monday", Icons.Default.Class, Orange)
            }
        }
    )
}

@Composable
private fun ExportDialog(students: List<Student>, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val csv = buildString {
        appendLine("Student,Roll Number,Status")
        students.forEach { appendLine("${it.name},${it.roll},${if (it.present) "Present" else "Absent"}") }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    context.startActivity(
                        Intent.createChooser(
                            Intent(Intent.ACTION_SEND).apply {
                                type = "text/csv"
                                putExtra(Intent.EXTRA_SUBJECT, "SmartAttend attendance report")
                                putExtra(Intent.EXTRA_TEXT, csv)
                            },
                            "Share attendance CSV"
                        )
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Blue)
            ) { Text("Share CSV") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("Export attendance", color = Navy) },
        text = {
            Column {
                Text("Your report is ready to share.", color = Color(0xFF627D98))
                Spacer(Modifier.height(12.dp))
                Text(csv, color = Navy, fontSize = 11.sp)
            }
        }
    )
}
