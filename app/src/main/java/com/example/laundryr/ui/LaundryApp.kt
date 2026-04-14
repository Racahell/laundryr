package com.example.laundryr.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.laundryr.data.model.OrderDto
import com.example.laundryr.data.model.UserDto
import com.example.laundryr.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaundryApp(viewModel: MainViewModel) {
    val uiState = viewModel.state.value
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    if (uiState.user == null) {
                        Text("Laundryr")
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Laundryr")
                            RoleBadge(uiState.user)
                        }
                    }
                },
                actions = {
                    if (uiState.user != null) {
                        TextButton(onClick = viewModel::logout) { Text("Logout") }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        val user = uiState.user
        if (user == null) {
            AuthPane(
                padding = innerPadding,
                loading = uiState.loading,
                registerSuccessVersion = uiState.registerSuccessVersion,
                viewModel = viewModel
            )
            return@Scaffold
        }

        when (user.role) {
            "admin" -> AdminReportPane(
                padding = innerPadding,
                orders = uiState.orders,
                onRefresh = viewModel::loadOrders
            )
            "staff" -> StaffPane(
                padding = innerPadding,
                orders = uiState.orders,
                loading = uiState.loading,
                orderCreatedVersion = uiState.orderCreatedVersion,
                viewModel = viewModel
            )
            else -> CustomerPane(
                padding = innerPadding,
                orders = uiState.orders,
                selectedInvoice = uiState.selectedInvoice,
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun RoleBadge(user: UserDto) {
    Text(
        text = user.role.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
private fun AuthPane(
    padding: PaddingValues,
    loading: Boolean,
    registerSuccessVersion: Int,
    viewModel: MainViewModel
) {
    var isLogin by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(registerSuccessVersion) {
        if (registerSuccessVersion > 0) {
            isLogin = true
            name = ""
            phone = ""
            address = ""
            password = ""
            passwordVisible = false
        }
    }

    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        if (isLogin) "Masuk ke Laundryr" else "Daftar Akun Laundryr",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (!isLogin) {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama") }, modifier = Modifier.fillMaxWidth())
                    }
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())

                    if (!isLogin) {
                        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("No HP") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Alamat") }, modifier = Modifier.fillMaxWidth())
                    }

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    painter = painterResource(if (passwordVisible) R.drawable.ic_invisible else R.drawable.ic_eye),
                                    contentDescription = if (passwordVisible) "Sembunyikan password" else "Tampilkan password"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            if (isLogin) viewModel.login(email, password)
                            else viewModel.register(name, email, phone, address, password)
                        },
                        enabled = !loading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (loading) "Memproses..." else if (isLogin) "Login" else "Register")
                    }

                    TextButton(onClick = { isLogin = !isLogin }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (isLogin) "Belum punya akun? Daftar" else "Sudah punya akun? Login")
                    }
                }
            }
        }
    }
}

@Composable
private fun StaffPane(
    padding: PaddingValues,
    orders: List<OrderDto>,
    loading: Boolean,
    orderCreatedVersion: Int,
    viewModel: MainViewModel,
) {
    val serviceTypeOptions = remember {
        listOf(
            1L to "Full Laundry",
            2L to "Setrika Saja",
        )
    }
    var selectedServiceType by remember { mutableStateOf(serviceTypeOptions.first()) }
    var serviceDropdownExpanded by remember { mutableStateOf(false) }
    var weight by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerAddress by remember { mutableStateOf("") }
    var selectedMenu by remember { mutableStateOf(0) } // 0 operasional, 1 belum lunas, 2 lunas
    var visibleCount by remember { mutableStateOf(20) }

    val filteredOrders = when (selectedMenu) {
        1 -> orders.filter { it.payment_status != "lunas" }
        2 -> orders.filter { it.payment_status == "lunas" }
        else -> emptyList()
    }
    val shownOrders = filteredOrders.take(visibleCount)

    LaunchedEffect(orders.size, selectedMenu) {
        visibleCount = minOf(20, filteredOrders.size)
    }

    LaunchedEffect(orderCreatedVersion) {
        if (orderCreatedVersion > 0) {
            selectedServiceType = serviceTypeOptions.first()
            weight = ""
            customerName = ""
            customerPhone = ""
            customerAddress = ""
        }
    }

    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            val tabs = listOf("Operasional", "Belum Lunas", "Lunas")
            TabRow(selectedTabIndex = selectedMenu) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedMenu == index,
                        onClick = { selectedMenu = index },
                        text = { Text(title) }
                    )
                }
            }
        }

        if (selectedMenu == 0) {
            item {
                Card {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Operasional Staff", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("Input pesanan baru dan update proses laundry.", style = MaterialTheme.typography.bodySmall)
                        Box {
                            OutlinedTextField(
                                value = selectedServiceType.second,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Service Type") },
                                trailingIcon = {
                                    TextButton(onClick = { serviceDropdownExpanded = true }) {
                                        Text("Pilih")
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            DropdownMenu(
                                expanded = serviceDropdownExpanded,
                                onDismissRequest = { serviceDropdownExpanded = false }
                            ) {
                                serviceTypeOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.second) },
                                        onClick = {
                                            selectedServiceType = option
                                            serviceDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Berat KG") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = customerName, onValueChange = { customerName = it }, label = { Text("Nama Customer") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = customerPhone, onValueChange = { customerPhone = it }, label = { Text("No HP") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = customerAddress, onValueChange = { customerAddress = it }, label = { Text("Alamat") }, modifier = Modifier.fillMaxWidth())
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    viewModel.createSampleOrder(
                                        serviceTypeId = selectedServiceType.first,
                                        weightKg = weight.toDoubleOrNull() ?: 0.0,
                                        customerName = customerName,
                                        customerPhone = customerPhone,
                                        customerAddress = customerAddress,
                                    )
                                },
                                enabled = !loading
                            ) { Text("Simpan Order") }
                            Button(onClick = viewModel::loadOrders, enabled = !loading) { Text("Refresh") }
                        }
                    }
                }
            }
        } else {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Text("Daftar Order", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    TextButton(onClick = viewModel::loadOrders) {
                        Text("Refresh")
                    }
                }
            }

            item {
                Text(
                    "Transaksi (${shownOrders.size}/${filteredOrders.size})",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            items(shownOrders, key = { it.id }) { order ->
                StaffOrderRow(order = order, onTransition = viewModel::transition, onVerify = viewModel::verifyPayment)
            }

            if (visibleCount < filteredOrders.size) {
                item {
                    Button(
                        onClick = { visibleCount = minOf(visibleCount + 20, filteredOrders.size) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Muat 20 lagi")
                    }
                }
            }
        }
    }
}

@Composable
private fun StaffOrderRow(
    order: OrderDto,
    onTransition: (Long, String) -> Unit,
    onVerify: (Long) -> Unit,
) {
    var expanded by remember(order.id) { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(order.invoice_number, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("Status: ${order.current_status} | Bayar: ${order.payment_status}")
            Text("Total: Rp${order.grand_total}")

            if (expanded) {
                Text("Customer: ${order.customer.name}")
                Text("Layanan: ${order.service_type.name}")
                if (order.overdue_unclaimed) {
                    Text("Overdue > 30 hari", color = MaterialTheme.colorScheme.error)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { onTransition(order.id, next(order.current_status, order.service_type.code)) }) {
                        Text("Next Status")
                    }
                    if (order.payment_status == "belum_lunas") {
                        Button(onClick = { onVerify(order.id) }) {
                            Text("Lunaskan")
                        }
                    }
                }
            }

            TextButton(onClick = { expanded = !expanded }) {
                Text(if (expanded) "Sembunyikan detail" else "Lihat detail")
            }
        }
    }
}

@Composable
private fun AdminReportPane(
    padding: PaddingValues,
    orders: List<OrderDto>,
    onRefresh: () -> Unit,
) {
    var selectedFilter by remember { mutableStateOf(0) } // 0: semua, 1: lunas, 2: belum lunas
    var visibleCount by remember { mutableStateOf(20) }

    val paidOrders = orders.count { it.payment_status == "lunas" }
    val unpaidOrders = orders.count { it.payment_status != "lunas" }
    val omzet = orders.filter { it.payment_status == "lunas" }.sumOf { it.grand_total }
    val filteredOrders = when (selectedFilter) {
        1 -> orders.filter { it.payment_status == "lunas" }
        2 -> orders.filter { it.payment_status != "lunas" }
        else -> orders
    }

    LaunchedEffect(orders.size, selectedFilter) {
        visibleCount = minOf(20, filteredOrders.size)
    }

    val shownOrders = filteredOrders.take(visibleCount)

    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Laporan Admin", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("Role admin hanya monitoring laporan transaksi.", style = MaterialTheme.typography.bodySmall)
                    Button(onClick = onRefresh) { Text("Refresh Data") }
                }
            }
        }

        item {
            val tabs = listOf("Semua", "Lunas", "Belum Lunas")
            TabRow(selectedTabIndex = selectedFilter) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedFilter == index,
                        onClick = { selectedFilter = index },
                        text = { Text(title) }
                    )
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    SummaryCard("Total Transaksi", orders.size.toString(), Modifier.weight(1f))
                    SummaryCard("Lunas", paidOrders.toString(), Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    SummaryCard("Belum Lunas", unpaidOrders.toString(), Modifier.weight(1f))
                    SummaryCard("Omzet", "Rp$omzet", Modifier.weight(1f))
                }
            }
        }

        item {
            Text(
                "Detail Transaksi (${shownOrders.size}/${filteredOrders.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        items(shownOrders, key = { it.id }) { order ->
            AdminOrderRow(order)
        }

        if (visibleCount < filteredOrders.size) {
            item {
                Button(
                    onClick = { visibleCount = minOf(visibleCount + 20, filteredOrders.size) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Muat 20 lagi")
                }
            }
        }
    }
}

@Composable
private fun AdminOrderRow(order: OrderDto) {
    var expanded by remember(order.id) { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(order.invoice_number, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("Status: ${order.current_status} | Bayar: ${order.payment_status}")
            Text("Total: Rp${order.grand_total}")

            if (expanded) {
                Text("Customer: ${order.customer.name}")
                Text("Layanan: ${order.service_type.name}")
                if (order.overdue_unclaimed) {
                    Text("Overdue > 30 hari", color = MaterialTheme.colorScheme.error)
                }
            }

            TextButton(onClick = { expanded = !expanded }) {
                Text(if (expanded) "Sembunyikan detail" else "Lihat detail")
            }
        }
    }
}

@Composable
private fun SummaryCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CustomerPane(
    padding: PaddingValues,
    orders: List<OrderDto>,
    selectedInvoice: OrderDto?,
    viewModel: MainViewModel,
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Riwayat Saya", "Cek Invoice", "Komplain")

    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
            }
        }

        when (selectedTab) {
            0 -> CustomerHistoryPane(orders, viewModel::loadOrders)
            1 -> CustomerInvoicePane(selectedInvoice, viewModel)
            else -> CustomerComplaintPane(viewModel)
        }
    }
}

@Composable
private fun CustomerHistoryPane(orders: List<OrderDto>, onRefresh: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Riwayat Laundry Saya", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("Data ini hanya menampilkan pesanan milik akun customer yang login.", style = MaterialTheme.typography.bodySmall)
                    Button(onClick = onRefresh) { Text("Refresh") }
                }
            }
        }

        items(orders) { order ->
            OrderCard(order = order, showActions = false, onTransition = { _, _ -> }, onVerify = {})
        }

        item {
            Text(
                "Ketentuan: Pesanan yang tidak diambil lebih dari 30 hari sejak status selesai tidak menjadi tanggung jawab pihak laundry.",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun CustomerInvoicePane(selectedInvoice: OrderDto?, viewModel: MainViewModel) {
    var invoice by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Cek Invoice", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(value = invoice, onValueChange = { invoice = it }, label = { Text("Nomor Invoice") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = { viewModel.lookupInvoice(invoice) }) { Text("Cek Invoice") }
                }
            }
        }

        if (selectedInvoice != null) {
            item { OrderCard(order = selectedInvoice, showActions = false, onTransition = { _, _ -> }, onVerify = {}) }
        }
    }
}

@Composable
private fun CustomerComplaintPane(viewModel: MainViewModel) {
    var invoice by remember { mutableStateOf("") }
    var complaintItem by remember { mutableStateOf("") }
    var damageType by remember { mutableStateOf("") }
    var complaintDesc by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Kirim Komplain", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(value = invoice, onValueChange = { invoice = it }, label = { Text("Nomor Invoice") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = complaintItem, onValueChange = { complaintItem = it }, label = { Text("Nama Barang") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = damageType, onValueChange = { damageType = it }, label = { Text("Jenis Kerusakan") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = complaintDesc, onValueChange = { complaintDesc = it }, label = { Text("Keterangan") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = {
                        viewModel.submitComplaint(invoice, complaintItem, damageType, complaintDesc)
                    }) { Text("Kirim Komplain") }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(
    order: OrderDto,
    showActions: Boolean,
    onTransition: (Long, String) -> Unit,
    onVerify: (Long) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(order.invoice_number, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("Customer: ${order.customer.name}")
            Text("Layanan: ${order.service_type.name}")
            Text("Status: ${order.current_status}")
            Text("Pembayaran: ${order.payment_status}")
            Text("Total: Rp${order.grand_total}")
            if (order.overdue_unclaimed) {
                Text("Overdue > 30 hari", color = MaterialTheme.colorScheme.error)
            }

            if (showActions) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { onTransition(order.id, next(order.current_status, order.service_type.code)) }) {
                        Text("Next Status")
                    }
                    if (order.payment_status == "belum_lunas") {
                        Button(onClick = { onVerify(order.id) }) {
                            Text("Lunaskan")
                        }
                    }
                }
            }
        }
    }
}

private fun next(current: String, serviceCode: String): String {
    val full = listOf("DITERIMA", "DICUCI", "DIJEMUR", "DISETRIKA", "SELESAI", "DIAMBIL")
    val iron = listOf("DITERIMA", "DISETRIKA", "SELESAI", "DIAMBIL")
    val flow = if (serviceCode == "SETRIKA_SAJA") iron else full
    val idx = flow.indexOf(current)
    return if (idx in 0 until flow.lastIndex) flow[idx + 1] else current
}
