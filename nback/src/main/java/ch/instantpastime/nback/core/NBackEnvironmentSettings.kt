package ch.instantpastime.nback.core

data class NBackEnvironmentSettings(val symbolType: SymbolType, val playSound: Boolean) {
    enum class SymbolType {
        Letter, Image
    }

    companion object {
        const val PLAY_SOUND_DEFAULT = false
    }
}
