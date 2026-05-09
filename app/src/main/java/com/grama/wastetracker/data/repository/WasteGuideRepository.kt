package com.grama.wastetracker.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.grama.wastetracker.data.model.WasteGuideItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface WasteGuideRepository {
    fun getWasteGuides(): Flow<List<WasteGuideItem>>
    suspend fun initializeDefaultGuides()
}

class WasteGuideRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : WasteGuideRepository {

    private val guidesRef = database.getReference("waste_guides")

    override fun getWasteGuides(): Flow<List<WasteGuideItem>> = callbackFlow {
        val listener = guidesRef.addValueEventListener(
            object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    val guides = snapshot.children.mapNotNull {
                        it.getValue(WasteGuideItem::class.java)
                    }
                    if (guides.isEmpty()) {
                        trySend(getDefaultGuides())
                    } else {
                        trySend(guides)
                    }
                }
                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                    trySend(getDefaultGuides())
                }
            }
        )
        awaitClose { guidesRef.removeEventListener(listener) }
    }

    override suspend fun initializeDefaultGuides() {
        try {
            val snapshot = guidesRef.get().await()
            if (!snapshot.exists()) {
                getDefaultGuides().forEach { guide ->
                    guidesRef.child(guide.id).setValue(guide).await()
                }
            }
        } catch (_: Exception) { }
    }

    private fun getDefaultGuides(): List<WasteGuideItem> = listOf(
        WasteGuideItem(
            id = "dry_waste",
            category = "dry",
            title = "Dry Waste",
            titleKn = "ಒಣ ತ್ಯಾಜ್ಯ",
            description = "Non-biodegradable waste that does not contain moisture",
            descriptionKn = "ತೇವಾಂಶವಿಲ್ಲದ ಜೈವಿಕ ವಿಘಟನೆಯಾಗದ ತ್ಯಾಜ್ಯ",
            items = listOf("Paper & Cardboard", "Plastic bottles & bags", "Metal cans", "Glass bottles", "Cloth & textiles", "Rubber", "Thermocol", "Tetra packs"),
            itemsKn = listOf("ಕಾಗದ ಮತ್ತು ಪೆಟ್ಟಿಗೆ", "ಪ್ಲಾಸ್ಟಿಕ್ ಬಾಟಲಿ ಮತ್ತು ಚೀಲ", "ಲೋಹದ ಡಬ್ಬ", "ಗಾಜಿನ ಬಾಟಲಿ", "ಬಟ್ಟೆ", "ರಬ್ಬರ್", "ಥರ್ಮೋಕೋಲ್", "ಟೆಟ್ರಾ ಪ್ಯಾಕ್"),
            disposalTip = "Keep dry waste separate and clean. Remove food residue before disposal.",
            disposalTipKn = "ಒಣ ತ್ಯಾಜ್ಯವನ್ನು ಪ್ರತ್ಯೇಕವಾಗಿ ಮತ್ತು ಸ್ವಚ್ಛವಾಗಿ ಇಡಿ.",
            iconName = "recycling"
        ),
        WasteGuideItem(
            id = "wet_waste",
            category = "wet",
            title = "Wet Waste",
            titleKn = "ಹಸಿ ತ್ಯಾಜ್ಯ",
            description = "Biodegradable waste that decomposes naturally",
            descriptionKn = "ನೈಸರ್ಗಿಕವಾಗಿ ಕೊಳೆಯುವ ಜೈವಿಕ ವಿಘಟನೆಯ ತ್ಯಾಜ್ಯ",
            items = listOf("Food scraps", "Vegetable & fruit peels", "Egg shells", "Tea leaves & coffee grounds", "Garden waste & leaves", "Flowers", "Cooked food leftovers"),
            itemsKn = listOf("ಆಹಾರ ತ್ಯಾಜ್ಯ", "ತರಕಾರಿ ಮತ್ತು ಹಣ್ಣಿನ ಸಿಪ್ಪೆ", "ಮೊಟ್ಟೆಯ ಚಿಪ್ಪು", "ಚಹಾ ಎಲೆ", "ತೋಟದ ತ್ಯಾಜ್ಯ", "ಹೂವುಗಳು", "ಉಳಿದ ಆಹಾರ"),
            disposalTip = "Use a green bin. Can be composted at home for garden manure.",
            disposalTipKn = "ಹಸಿರು ಬಿನ್ ಬಳಸಿ. ತೋಟದ ಗೊಬ್ಬರಕ್ಕಾಗಿ ಮನೆಯಲ್ಲಿ ಕಾಂಪೋಸ್ಟ್ ಮಾಡಬಹುದು.",
            iconName = "compost"
        ),
        WasteGuideItem(
            id = "hazardous_waste",
            category = "hazardous",
            title = "Hazardous Waste",
            titleKn = "ಅಪಾಯಕಾರಿ ತ್ಯಾಜ್ಯ",
            description = "Waste that is dangerous to health and environment",
            descriptionKn = "ಆರೋಗ್ಯ ಮತ್ತು ಪರಿಸರಕ್ಕೆ ಅಪಾಯಕಾರಿ ತ್ಯಾಜ್ಯ",
            items = listOf("Batteries", "Electronic waste", "Expired medicines", "Paints & solvents", "CFL & LED bulbs", "Pesticide containers", "Syringes & needles"),
            itemsKn = listOf("ಬ್ಯಾಟರಿಗಳು", "ಎಲೆಕ್ಟ್ರಾನಿಕ್ ತ್ಯಾಜ್ಯ", "ಅವಧಿ ಮೀರಿದ ಔಷಧಿ", "ಬಣ್ಣ", "CFL ಮತ್ತು LED ಬಲ್ಬ್", "ಕೀಟನಾಶಕ ಪಾತ್ರೆ", "ಸಿರಿಂಜ್ ಮತ್ತು ಸೂಜಿ"),
            disposalTip = "Never mix with regular waste. Hand over to authorized collection centers.",
            disposalTipKn = "ಸಾಮಾನ್ಯ ತ್ಯಾಜ್ಯದೊಂದಿಗೆ ಬೆರೆಸಬೇಡಿ. ಅಧಿಕೃತ ಸಂಗ್ರಹಣಾ ಕೇಂದ್ರಕ್ಕೆ ಒಪ್ಪಿಸಿ.",
            iconName = "warning"
        )
    )
}
