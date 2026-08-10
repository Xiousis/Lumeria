package com.solerforge.lumeria.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.managers.GuildManager
import com.solerforge.lumeria.models.Guild
import com.solerforge.lumeria.models.GuildMember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GuildViewModel(private val guildManager: GuildManager) : ViewModel() {
    private val _guildList = MutableStateFlow<List<Guild>>(emptyList())
    val guildList: StateFlow<List<Guild>> = _guildList

    private val _currentGuild = MutableStateFlow<Guild?>(null)
    val currentGuild: StateFlow<Guild?> = _currentGuild

    private val _guildMembers = MutableStateFlow<List<GuildMember>>(emptyList())
    val guildMembers: StateFlow<List<GuildMember>> = _guildMembers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchGuilds() {
        viewModelScope.launch {
            _isLoading.value = true
            guildManager.fetchAllGuilds().onSuccess {
                _guildList.value = it
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun fetchCurrentGuild(guildId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            guildManager.getGuild(guildId).onSuccess {
                _currentGuild.value = it
            }.onFailure {
                _error.value = it.message
            }
            guildManager.getGuildMembers(guildId).onSuccess {
                _guildMembers.value = it
            }
            _isLoading.value = false
        }
    }

    fun createGuild(name: String, player: PlayerData, deviceId: String, onComplete: (String, String) -> Unit) {
        if (deviceId.isBlank() || deviceId == "unknown") {
            _error.value = "Cannot create guild: Device identity unknown. Please check your connection."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            guildManager.createGuild(name, player, deviceId).onSuccess { guildId ->
                onComplete(guildId, name)
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun joinGuild(guildId: String, guildName: String, player: PlayerData, deviceId: String, onComplete: (String, String) -> Unit) {
        if (deviceId.isBlank() || deviceId == "unknown") {
            _error.value = "Cannot join guild: Device identity unknown. Please check your connection."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            guildManager.joinGuild(guildId, player, deviceId).onSuccess {
                onComplete(guildId, guildName)
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun donate(guildId: String, amount: Long, deviceId: String, onComplete: () -> Unit) {
        if (deviceId.isBlank() || deviceId == "unknown") {
            _error.value = "Cannot donate: Device identity unknown. Please check your connection."
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            guildManager.donateToVault(guildId, amount, deviceId).onSuccess {
                onComplete()
                fetchCurrentGuild(guildId) // Refresh
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}

class GuildViewModelFactory(private val guildManager: GuildManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GuildViewModel(guildManager) as T
    }
}
