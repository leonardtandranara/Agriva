package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.MarketItem
import com.example.data.model.WalletTransaction
import com.example.data.model.WeatherData
import com.example.data.repository.AgrivaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppScreen {
    AUTH,
    DASHBOARD
}

enum class DashboardTab {
    DASHBOARD_SUMMARY,
    WEATHER,
    MARKET,
    WALLET,
    MAP,
    TRASH
}

class AgrivaViewModel(
    application: Application,
    private val repository: AgrivaRepository
) : AndroidViewModel(application) {

    // === Navigation State ===
    private val _currentScreen = MutableStateFlow(AppScreen.AUTH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentTab = MutableStateFlow(DashboardTab.DASHBOARD_SUMMARY)
    val currentTab: StateFlow<DashboardTab> = _currentTab.asStateFlow()

    // === Authentication State (RBAC) ===
    private val _userRole = MutableStateFlow<String?>(null) // "ADMIN" or "USER"
    val userRole: StateFlow<String?> = _userRole.asStateFlow()

    private val _currentUserEmail = MutableStateFlow<String?>(null)
    val currentUserEmail: StateFlow<String?> = _currentUserEmail.asStateFlow()

    private val _authStateError = MutableStateFlow<String?>(null)
    val authStateError: StateFlow<String?> = _authStateError.asStateFlow()

    private val _isAuthenticating = MutableStateFlow(false)
    val isAuthenticating: StateFlow<Boolean> = _isAuthenticating.asStateFlow()

    // === Database Flow Streams ===
    val activeMarketItems: StateFlow<List<MarketItem>> = repository.activeMarketItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deletedMarketItems: StateFlow<List<MarketItem>> = repository.deletedMarketItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeTransactions: StateFlow<List<WalletTransaction>> = repository.activeTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deletedTransactions: StateFlow<List<WalletTransaction>> = repository.deletedTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWeather: StateFlow<List<WeatherData>> = repository.allWeather
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // === Derived State / Wallet Balance ===
    val walletBalance: StateFlow<Double> = activeTransactions
        .map { transactions ->
            transactions.firstOrNull()?.balanceAfter ?: 250000.0 // Default simulation fallback
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 250000.0)


    // === Filter & Interactions State ===
    private val _marketSearchQuery = MutableStateFlow("")
    val marketSearchQuery: StateFlow<String> = _marketSearchQuery.asStateFlow()

    private val _marketRegionFilter = MutableStateFlow("TOUT") // "TOUT" or region name
    val marketRegionFilter: StateFlow<String> = _marketRegionFilter.asStateFlow()

    private val _selectedMapRegion = MutableStateFlow("Androy")
    val selectedMapRegion: StateFlow<String> = _selectedMapRegion.asStateFlow()


    init {
        prepopulateDataIfNeeded()
        // Run persistent garbage collector check on startup
        checkAndSweepOldTrash()
    }

    // === PREPOPULATION LOGIC (Offline-First Starter Pack) ===
    private fun prepopulateDataIfNeeded() {
        viewModelScope.launch {
            // Prepopulate Weather
            val weatherRecords = repository.allWeather.first()
            if (weatherRecords.isEmpty()) {
                repository.insertWeather(
                    WeatherData(
                        region = "Atsimo-Andrefana",
                        temperature = 28.5,
                        rain = 12.0,
                        wind = 18.0,
                        humidity = 65.0,
                        riskLevel = "VERT"
                    )
                )
                repository.insertWeather(
                    WeatherData(
                        region = "Androy",
                        temperature = 34.2,
                        rain = 0.0,
                        wind = 25.0,
                        humidity = 20.0,
                        riskLevel = "ROUGE" // Urgent Drought Alert
                    )
                )
                repository.insertWeather(
                    WeatherData(
                        region = "Anosy",
                        temperature = 24.0,
                        rain = 45.0,
                        wind = 32.0,
                        humidity = 85.0,
                        riskLevel = "JAUNE" // Heavy Local Rains
                    )
                )
                repository.insertWeather(
                    WeatherData(
                        region = "Ihorombe",
                        temperature = 21.8,
                        rain = 2.5,
                        wind = 42.0,
                        humidity = 40.0,
                        riskLevel = "ORANGE" // Strong Winds Forecast
                    )
                )
            }

            // Prepopulate Market
            val marketRecords = repository.activeMarketItems.first()
            if (marketRecords.isEmpty()) {
                repository.insertMarketItem(
                    MarketItem(
                        name = "Riz Gasy",
                        price = 3200.0,
                        region = "Atsimo-Andrefana",
                        variation = 1.5,
                        status = "active"
                    )
                )
                repository.insertMarketItem(
                    MarketItem(
                        name = "Maïs",
                        price = 1800.0,
                        region = "Androy",
                        variation = 5.2,
                        status = "active"
                    )
                )
                repository.insertMarketItem(
                    MarketItem(
                        name = "Manioc Séché",
                        price = 1500.0,
                        region = "Anosy",
                        variation = -2.3,
                        status = "active"
                    )
                )
                repository.insertMarketItem(
                    MarketItem(
                        name = "Haricots Lingot",
                        price = 4000.0,
                        region = "Ihorombe",
                        variation = 0.4,
                        status = "active"
                    )
                )
                repository.insertMarketItem(
                    MarketItem(
                        name = "Arachide",
                        price = 3500.0,
                        region = "Atsimo-Andrefana",
                        variation = -1.2,
                        status = "active"
                    )
                )
                repository.insertMarketItem(
                    MarketItem(
                        name = "Riz Makalioka",
                        price = 3800.0,
                        region = "Ihorombe",
                        variation = 2.1,
                        status = "inactive"
                    )
                )
            }

            // Prepopulate Wallet
            val transactions = repository.activeTransactions.first()
            if (transactions.isEmpty()) {
                repository.insertTransaction(
                    WalletTransaction(
                        type = "credit",
                        amount = 150000.0,
                        description = "Vente de 50kg de Riz Gasy à Atsimo-Andrefana",
                        balanceAfter = 150000.0,
                        createdAt = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000 // 2 days ago
                    )
                )
                repository.insertTransaction(
                    WalletTransaction(
                        type = "debit",
                        amount = 45000.0,
                        description = "Achat de semences sélectionnées AGRIVA",
                        balanceAfter = 105000.0,
                        createdAt = System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000 // 1 day ago
                    )
                )
                repository.insertTransaction(
                    WalletTransaction(
                        type = "credit",
                        amount = 145000.0,
                        description = "Régulation du marché - Récolte de Maïs",
                        balanceAfter = 250000.0,
                        createdAt = System.currentTimeMillis() // Today
                    )
                )
            }
        }
    }


    // === authentication logic (RBAC auth simulation) ===
    fun login(email: String, role: String) {
        _authStateError.value = null
        if (email.isBlank() || !email.contains("@")) {
            _authStateError.value = "Hafidiro mailaka manan-kery azafady!"
            return
        }

        viewModelScope.launch {
            _isAuthenticating.value = true
            kotlinx.coroutines.delay(600) // Aesthetic network latency simulation
            _userRole.value = role
            _currentUserEmail.value = email
            _currentScreen.value = AppScreen.DASHBOARD
            _isAuthenticating.value = false
        }
    }

    fun logout() {
        _userRole.value = null
        _currentUserEmail.value = null
        _currentScreen.value = AppScreen.AUTH
        _currentTab.value = DashboardTab.DASHBOARD_SUMMARY
    }


    // === MARKET CRUD SYSTEM (Universal) ===
    fun setMarketFilters(search: String, region: String) {
        _marketSearchQuery.value = search
        _marketRegionFilter.value = region
    }

    fun addMarketItem(name: String, price: Double, region: String, variation: Double): Boolean {
        if (userRole.value != "ADMIN") return false // Role guard
        if (name.isBlank() || price <= 0) return false

        viewModelScope.launch {
            repository.insertMarketItem(
                MarketItem(
                    name = name,
                    price = price,
                    region = region,
                    variation = variation,
                    status = "active"
                )
            )
        }
        return true
    }

    fun updateMarketItem(item: MarketItem): Boolean {
        if (userRole.value != "ADMIN") return false // Role guard
        viewModelScope.launch {
            repository.updateMarketItem(item.copy(updatedAt = System.currentTimeMillis()))
        }
        return true
    }

    fun toggleMarketItemStatus(item: MarketItem): Boolean {
        if (userRole.value != "ADMIN") return false
        val newStatus = if (item.status == "active") "inactive" else "active"
        viewModelScope.launch {
            repository.updateMarketItem(item.copy(status = newStatus, updatedAt = System.currentTimeMillis()))
        }
        return true
    }

    fun softDeleteMarketItem(id: Long): Boolean {
        if (userRole.value != "ADMIN") return false
        viewModelScope.launch {
            repository.softDeleteMarketItem(id)
        }
        return true
    }

    fun restoreMarketItem(id: Long): Boolean {
        if (userRole.value != "ADMIN") return false
        viewModelScope.launch {
            repository.restoreMarketItem(id)
        }
        return true
    }

    fun deleteMarketItemPermanent(id: Long): Boolean {
        if (userRole.value != "ADMIN") return false
        viewModelScope.launch {
            repository.deleteMarketItemPermanent(id)
        }
        return true
    }


    // === WALLET SIMULATOR ===
    fun simulateWalletTransaction(type: String, amount: Double, description: String): Boolean {
        if (amount <= 0 || description.isBlank()) return false
        val currentBalance = walletBalance.value

        if (type == "debit" && currentBalance < amount) {
            // Cannot debit more than is available
            return false
        }

        val nextBalance = if (type == "credit") {
            currentBalance + amount
        } else {
            currentBalance - amount
        }

        viewModelScope.launch {
            repository.insertTransaction(
                WalletTransaction(
                    type = type,
                    amount = amount,
                    description = description,
                    balanceAfter = nextBalance
                )
            )
        }
        return true
    }

    fun softDeleteTransaction(transaction: WalletTransaction): Boolean {
        if (userRole.value != "ADMIN") return false
        viewModelScope.launch {
            repository.softDeleteTransaction(transaction)
        }
        return true
    }

    fun restoreTransaction(transaction: WalletTransaction): Boolean {
        if (userRole.value != "ADMIN") return false
        viewModelScope.launch {
            repository.restoreTransaction(transaction)
        }
        return true
    }

    fun deleteTransactionPermanent(id: Long): Boolean {
        if (userRole.value != "ADMIN") return false
        viewModelScope.launch {
            repository.deleteTransactionPermanent(id)
        }
        return true
    }


    // === MAP & RISK FORECAST ===
    fun updateRegionRisk(region: String, risk: String, temp: Double, rain: Double, wind: Double, hum: Double): Boolean {
        if (userRole.value != "ADMIN") return false
        viewModelScope.launch {
            repository.insertWeather(
                WeatherData(
                    region = region,
                    temperature = temp,
                    rain = rain,
                    wind = wind,
                    humidity = hum,
                    riskLevel = risk,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
        return true
    }

    fun selectMapRegion(region: String) {
        _selectedMapRegion.value = region
    }


    // === TRASH CORBEILLE SYSTEM & AUTO SWEEP (30 DAYS) ===
    fun checkAndSweepOldTrash() {
        viewModelScope.launch {
            val thirtyDaysAgo = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
            repository.cleanMarketTrash(thirtyDaysAgo)
            repository.cleanWalletTrash(thirtyDaysAgo)
        }
    }

    // Interactive simulator function: shifts all soft-deleted items' deletion date backward 
    // by 35 days, allowing immediate demonstration of the 30-day automatic GC sweep!
    fun simulateAgingOlderThan30Days() {
        if (userRole.value != "ADMIN") return
        viewModelScope.launch {
            val ageShift = 35L * 24 * 60 * 60 * 1000
            val mockDeletedAt = System.currentTimeMillis() - ageShift

            // Fetch and update delete timestamp in db for market items
            val deletedItems = repository.deletedMarketItems.first()
            for (item in deletedItems) {
                repository.updateMarketItem(item.copy(deletedAt = mockDeletedAt))
            }

            // Fetch and update delete timestamp in db for wallet transactions
            val deletedTransactions = repository.deletedTransactions.first()
            for (tx in deletedTransactions) {
                repository.softDeleteTransaction(tx.copy(deletedAt = mockDeletedAt))
            }
        }
    }

    // Force sweep instantly - bypasses 30 days timer for immediate testing/cleaning
    fun forceImmediateTrashClean() {
        if (userRole.value != "ADMIN") return
        viewModelScope.launch {
            val now = System.currentTimeMillis() + 1000 // delete everything currently in trash
            repository.cleanMarketTrash(now)
            repository.cleanWalletTrash(now)
        }
    }


    // === TAB CONTROL ===
    fun selectTab(tab: DashboardTab) {
        _currentTab.value = tab
    }
}

class AgrivaViewModelFactory(
    private val application: Application,
    private val repository: AgrivaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgrivaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AgrivaViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
