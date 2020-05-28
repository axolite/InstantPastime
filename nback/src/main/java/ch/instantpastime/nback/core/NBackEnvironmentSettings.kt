package ch.instantpastime.nback.core

data class NBackEnvironmentSettings(val symbolType: SymbolType, val playSound: Boolean) {
    enum class SymbolType {
        Letter, Image
    }
}
