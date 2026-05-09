package com.grama.wastetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grama.wastetracker.data.model.ChatMessage
import com.grama.wastetracker.data.model.WasteClassification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AiAssistantUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isThinking: Boolean = false,
    val classification: WasteClassification? = null,
    val error: String? = null
)

@HiltViewModel
class AiAssistantViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AiAssistantUiState())
    val uiState: StateFlow<AiAssistantUiState> = _uiState.asStateFlow()

    // Knowledge base for waste classification
    private val wasteKnowledge = mapOf(
        "dry" to listOf("paper", "cardboard", "plastic", "bottle", "can", "metal", "glass", "cloth", "textile", "rubber", "thermocol", "tetra", "wrapper", "bag", "box", "carton", "foil", "tin", "jar"),
        "wet" to listOf("food", "vegetable", "fruit", "peel", "scraps", "leaves", "flower", "garden", "tea", "coffee", "egg", "shell", "cooked", "rice", "bread", "leftover", "organic", "compost", "banana", "mango"),
        "hazardous" to listOf("battery", "electronic", "e-waste", "medicine", "tablet", "paint", "chemical", "bulb", "cfl", "led", "pesticide", "syringe", "needle", "acid", "bleach", "oil", "motor", "thermometer", "mercury")
    )

    private val disposalAdvice = mapOf(
        "dry" to "Collect dry waste separately in a bag. Remove any food residue. Most dry waste can be recycled — sell to local recyclers or hand over to waste collectors on dry waste collection day.",
        "wet" to "Use a green bin for wet waste. You can compost it at home to create organic manure for your garden. Never mix wet waste with dry waste.",
        "hazardous" to "⚠️ Handle with care! Never throw hazardous waste in regular bins. Store separately and hand over to authorized collection centers. Contact your Gram Panchayat for collection drives."
    )

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(content = text, isFromUser = true)
        _uiState.update { it.copy(messages = it.messages + userMsg, isThinking = true) }

        viewModelScope.launch {
            delay(800) // Simulate AI processing
            val response = generateResponse(text)
            val botMsg = ChatMessage(content = response, isFromUser = false)
            _uiState.update { it.copy(messages = it.messages + botMsg, isThinking = false) }
        }
    }

    fun classifyWaste(description: String) {
        if (description.isBlank()) return
        _uiState.update { it.copy(isThinking = true) }

        viewModelScope.launch {
            delay(1000) // Simulate AI classification
            val words = description.lowercase().split(" ", ",", ".", "-", "/")
            var bestCategory = "dry"
            var maxScore = 0
            var matchedTerms = mutableListOf<String>()

            for ((category, keywords) in wasteKnowledge) {
                val score = words.count { word -> keywords.any { it in word || word in it } }
                if (score > maxScore) {
                    maxScore = score
                    bestCategory = category
                    matchedTerms = words.filter { word -> keywords.any { it in word || word in it } }.toMutableList()
                }
            }

            val confidence = when {
                maxScore >= 3 -> 0.95f
                maxScore == 2 -> 0.85f
                maxScore == 1 -> 0.70f
                else -> 0.50f
            }

            val categoryName = when (bestCategory) {
                "dry" -> "Dry Waste ♻️"
                "wet" -> "Wet Waste 🌿"
                "hazardous" -> "Hazardous Waste ⚠️"
                else -> "Unknown"
            }

            val explanation = if (matchedTerms.isNotEmpty()) {
                "Based on keywords: ${matchedTerms.joinToString(", ")}. This item is classified as $categoryName."
            } else {
                "Based on general analysis, this appears to be $categoryName. Please verify with local guidelines."
            }

            val classification = WasteClassification(
                category = bestCategory,
                confidence = confidence,
                explanation = explanation,
                disposalAdvice = disposalAdvice[bestCategory] ?: ""
            )
            _uiState.update { it.copy(classification = classification, isThinking = false) }
        }
    }

    private fun generateResponse(query: String): String {
        val q = query.lowercase()
        return when {
            q.contains("how") && (q.contains("segregat") || q.contains("separat") || q.contains("sort")) ->
                "🗑️ **Waste Segregation Guide:**\n\n" +
                "1. **Green Bin** — Wet waste (food scraps, peels, leaves)\n" +
                "2. **Blue Bin** — Dry waste (paper, plastic, metal)\n" +
                "3. **Red Bin** — Hazardous waste (batteries, medicines)\n\n" +
                "Always keep bins separate and clean. Rinse containers before putting in dry waste bin."

            q.contains("compost") || q.contains("manure") || q.contains("fertiliz") ->
                "🌱 **Home Composting Tips:**\n\n" +
                "1. Collect wet waste (food scraps, peels) in a pit or bin\n" +
                "2. Add dry leaves as a brown layer\n" +
                "3. Keep it moist but not wet\n" +
                "4. Turn the pile every week\n" +
                "5. Compost is ready in 45-60 days\n\n" +
                "Great organic manure for your garden! 🪴"

            q.contains("plastic") || q.contains("recycle") ->
                "♻️ **Plastic & Recycling:**\n\n" +
                "• Rinse plastic containers before recycling\n" +
                "• Avoid single-use plastics\n" +
                "• Carry cloth bags for shopping\n" +
                "• Plastic bottles can be sold to local recyclers\n" +
                "• Reduce, Reuse, Recycle!"

            q.contains("battery") || q.contains("electronic") || q.contains("e-waste") ->
                "⚠️ **E-Waste & Batteries:**\n\n" +
                "• Never throw in regular bins!\n" +
                "• Store separately in a dry place\n" +
                "• Contact authorized e-waste collectors\n" +
                "• Many brands offer take-back programs\n" +
                "• Gram Panchayat organizes collection drives"

            q.contains("tractor") || q.contains("collection") || q.contains("pickup") || q.contains("gaadi") ->
                "🚜 **Waste Collection:**\n\n" +
                "• Check the Home screen for live tractor location\n" +
                "• You'll receive a notification when it's nearby\n" +
                "• Keep segregated waste ready at your doorstep\n" +
                "• Wet waste: Green bag, Dry waste: Blue bag"

            q.contains("blackspot") || q.contains("dump") || q.contains("illegal") ->
                "📍 **Report Illegal Dumping:**\n\n" +
                "1. Go to 'Report Blackspot' in the app\n" +
                "2. Take a photo of the dumping site\n" +
                "3. GPS location is captured automatically\n" +
                "4. Add a description\n" +
                "5. Submit — Panchayat will take action!\n\n" +
                "Together we can keep our village clean! 🌿"

            q.contains("hello") || q.contains("hi") || q.contains("namask") ->
                "🙏 Namaskara! I'm your Waste Management Assistant.\n\n" +
                "I can help you with:\n" +
                "• Waste segregation guidance\n" +
                "• Composting tips\n" +
                "• Recycling information\n" +
                "• Reporting illegal dumping\n\n" +
                "What would you like to know?"

            else ->
                "🤔 I can help you with waste management queries!\n\n" +
                "Try asking about:\n" +
                "• How to segregate waste\n" +
                "• Composting at home\n" +
                "• Plastic recycling\n" +
                "• Battery disposal\n" +
                "• Reporting blackspots\n" +
                "• Waste collection schedule"
        }
    }

    fun clearClassification() {
        _uiState.update { it.copy(classification = null) }
    }
}
