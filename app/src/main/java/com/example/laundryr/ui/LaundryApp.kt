package com.example.laundryr.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.unit.dp
import com.example.laundryr.data.model.OrderDto

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
                    Text(if (uiState.user == null) "Laundryr" else "Laundryr - ${uiState.user.role.uppercase()}")
                },
                actions = {
                    if (uiState.user != null) {
                        TextButton(onClick = viewModel::logout) {
                            Text("Logout")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        if (uiState.user == null) {
            AuthPane(innerPadding, uiState.loading, viewModel)
        } else {
            when (uiState.user.role) {
                "admin", "staff" -> StaffAdminPane(innerPadding, uiState.orders, viewModel)
                else -> CustomerPane(innerPadding, uiState.orders, uiState.selectedInvoice, viewModel)
            }
        }
    }
}

@Composable
private fun AuthPane(padding: PaddingValues, loading: Boolean, viewModel: MainViewModel) {
    var isLogin by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("admin@laundryr.local") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("password123") }

    Column(
        modifier = Modifier
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(if (isLogin) "Masuk" else "Daftar", style = MaterialTheme.typography.headlineSmall)

        if (!isLogin) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama") }, modifier = Modifier.fillMaxWidth())
        }
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        if (!isLogin) {
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("No HP") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Alamat") }, modifier = Modifier.fillMaxWidth())
        }
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())

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

@Composable
private fun StaffAdminPane(padding: PaddingValues, orders: List<OrderDto>, viewModel: MainViewModel) {
    var serviceTypeId by remember { mutableStateOf("1") }
    var weight by remember { mutableStateOf("3") }
    var customerName by remember { mutableStateOf("Customer Walk-In") }
    var customerPhone by remember { mutableStateOf("08123456789") }
    var customerAddress by remember { mutableStateOf("Alamat customer") }

    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tambah Pesanan", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(value = serviceTypeId, onValueChange = { serviceTypeId = it }, label = { Text("Service Type ID (1 Full / 2 Setrika)") })
                    OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Berat KG") })
                    OutlinedTextField(value = customerName, onValueChange = { customerName = it }, label = { Text("Nama Customer") })
                    OutlinedTextField(value = customerPhone, onValueChange = { customerPhone = it }, label = { Text("No HP") })
                    OutlinedTextField(value = customerAddress, onValueChange = { customerAddress = it }, label = { Text("Alamat") })
                    Button(onClick = {
                        viewModel.createSampleOrder(
                            serviceTypeId = serviceTypeId.toLongOrNull() ?: 1L,
                            weightKg = weight.toDoubleOrNull() ?: 0.0,
                            customerName = customerName,
                            customerPhone = customerPhone,
                            customerAddress = customerAddress,
                        )
                    }) {
                        Text("Simpan Order")
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = viewModel::loadOrders) { Text("Refresh") }
            }
        }

        items(orders) { order ->
            OrderCard(order = order, onTransition = viewModel::transition, onVerify = viewModel::verifyPayment)
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
    var invoice by remember { mutableStateOf("") }
    var complaintItem by remember { mutableStateOf("") }
    var damageType by remember { mutableStateOf("") }
    var complaintDesc by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Cek Status Laundry", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(value = invoice, onValueChange = { invoice = it }, label = { Text("Nomor Invoice") })
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { viewModel.lookupInvoice(invoice) }) { Text("Cek Invoice") }
                        Button(onClick = viewModel::loadOrders) { Text("Refresh Pesanan") }
                    }
                }
            }
        }

        if (selectedInvoice != null) {
            item {
                OrderCard(order = selectedInvoice, onTransition = { _, _ -> }, onVerify = {})
            }
        }

        item {
            Card {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Laporan Kerusakan", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(value = complaintItem, onValueChange = { complaintItem = it }, label = { Text("Nama Barang") })
                    OutlinedTextField(value = damageType, onValueChange = { damageType = it }, label = { Text("Jenis Kerusakan") })
                    OutlinedTextField(value = complaintDesc, onValueChange = { complaintDesc = it }, label = { Text("Keterangan") })
                    Button(onClick = {
                        viewModel.submitComplaint(invoice, complaintItem, damageType, complaintDesc)
                    }) {
                        Text("Kirim Komplain")
                    }
                }
            }
        }

        item {
            Text("Riwayat Pesanan", style = MaterialTheme.typography.titleMedium)
        }
        items(orders) { order ->
            OrderCard(order = order, onTransition = { _, _ -> }, onVerify = {})
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
private fun OrderCard(
    order: OrderDto,
    onTransition: (Long, String) -> Unit,
    onVerify: (Long) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(order.invoice_number, style = MaterialTheme.typography.titleMedium)
            Text("Layanan: ${order.service_type.name}")
            Text("Status: ${order.current_status}")
            Text("Pembayaran: ${order.payment_status}")
            Text("Total: Rp${order.grand_total}")
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
    }
}

private fun next(current: String, serviceCode: String): String {
    val full = listOf("DITERIMA", "DICUCI", "DIJEMUR", "DISETRIKA", "SELESAI", "DIAMBIL")
    val iron = listOf("DITERIMA", "DISETRIKA", "SELESAI", "DIAMBIL")
    val flow = if (serviceCode == "SETRIKA_SAJA") iron else full
    val idx = flow.indexOf(current)
    return if (idx in 0 until flow.lastIndex) flow[idx + 1] else current
}
