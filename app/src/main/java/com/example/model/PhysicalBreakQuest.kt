package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.SleekCoral
import com.example.ui.theme.SleekCoralDark
import com.example.ui.theme.SleekEmerald
import com.example.ui.theme.SleekEmeraldDark
import com.example.ui.theme.SleekGold
import com.example.ui.theme.SleekGoldDark
import com.example.ui.theme.SleekOcean
import com.example.ui.theme.SleekOceanDark
import com.example.ui.theme.SleekPurple

/**
 * PhysicalBreakQuest
 *
 * Offline, active physical challenges suggested by Lumi periodically to reduce
 * sedentary screen time while playing on Android TV.
 */
data class PhysicalBreakQuest(
    val id: String,
    val title: String,
    val description: String,
    val spokenPrompt: String,
    val emoji: String,
    val durationSeconds: Int = 15,
    val rewardPoints: Int = 20,
    val color: Color = SleekEmerald,
    val colorDark: Color = SleekEmeraldDark
)

object PhysicalBreakCatalog {
    val allQuests: List<PhysicalBreakQuest> = listOf(
        PhysicalBreakQuest(
            id = "find_blue",
            title = "Find Something Blue!",
            description = "Get off the couch and touch or hold something BLUE in your room!",
            spokenPrompt = "Time for a fun movement break! Stand up and find something blue in your room! Let's go!",
            emoji = "🔵",
            durationSeconds = 15,
            rewardPoints = 25,
            color = SleekOcean,
            colorDark = SleekOceanDark
        ),
        PhysicalBreakQuest(
            id = "touch_toes",
            title = "Touch Your Toes!",
            description = "Bend down and touch your toes 5 times like a gymnast!",
            spokenPrompt = "Let's stretch! Stand up tall and touch your toes five times! One, two, three, four, five!",
            emoji = "🦶",
            durationSeconds = 15,
            rewardPoints = 20,
            color = SleekCoral,
            colorDark = SleekCoralDark
        ),
        PhysicalBreakQuest(
            id = "count_shoes",
            title = "Count 5 Shoes!",
            description = "Walk around and count 5 shoes in your home!",
            spokenPrompt = "Off-screen quest! Walk over and count five shoes in your home! One, two, three, four, five!",
            emoji = "👟",
            durationSeconds = 15,
            rewardPoints = 25,
            color = SleekOcean,
            colorDark = SleekOceanDark
        ),
        PhysicalBreakQuest(
            id = "hop_three",
            title = "Hop 3 Times!",
            description = "Jump up into the air and hop 3 times on one foot!",
            spokenPrompt = "Action time! Jump up in the air and hop three times! Ready, set, hop!",
            emoji = "🦘",
            durationSeconds = 10,
            rewardPoints = 20,
            color = SleekGold,
            colorDark = SleekGoldDark
        ),
        PhysicalBreakQuest(
            id = "hop_bunny",
            title = "Hop Like a Bunny!",
            description = "Jump up and down 4 times like a cute bunny rabbit!",
            spokenPrompt = "Hop hop hop! Can you jump high like a happy bunny four times?",
            emoji = "🐰",
            durationSeconds = 12,
            rewardPoints = 20,
            color = SleekGold,
            colorDark = SleekGoldDark
        ),
        PhysicalBreakQuest(
            id = "high_five",
            title = "Give a High-Five!",
            description = "Give a big high-five to someone near you, or high-five Lumi on the TV!",
            spokenPrompt = "High five time! Give a huge high five to someone in the room, or wave high five to Lumi!",
            emoji = "✋",
            durationSeconds = 10,
            rewardPoints = 20,
            color = SleekPurple,
            colorDark = Color(0xFF5B1AA8)
        ),
        PhysicalBreakQuest(
            id = "reach_sky",
            title = "Reach for the Sky!",
            description = "Stretch your hands as high as a giraffe reaching for tall leaves!",
            spokenPrompt = "Reach way up high to the sky! Stretch like a super tall giraffe!",
            emoji = "🦒",
            durationSeconds = 12,
            rewardPoints = 20,
            color = SleekEmerald,
            colorDark = SleekEmeraldDark
        ),
        PhysicalBreakQuest(
            id = "find_round",
            title = "Find Something Round!",
            description = "Look around your room for something round like a ball, clock, or coaster!",
            spokenPrompt = "Detective mission! Look around your room and find something round like a circle!",
            emoji = "⚽",
            durationSeconds = 15,
            rewardPoints = 25,
            color = SleekOcean,
            colorDark = SleekOceanDark
        ),
        PhysicalBreakQuest(
            id = "freeze_statue",
            title = "Freeze Like a Statue!",
            description = "Strike your coolest hero pose and hold completely still for 5 seconds!",
            spokenPrompt = "Freeze! Strike a superhero pose and hold totally still like a statue! Ready, set, freeze!",
            emoji = "🦸",
            durationSeconds = 10,
            rewardPoints = 20,
            color = SleekCoral,
            colorDark = SleekCoralDark
        ),
        PhysicalBreakQuest(
            id = "spin_celebrate",
            title = "Spin & Dance!",
            description = "Do one gentle spin around and shake your happy dance hands!",
            spokenPrompt = "Let's do a happy spin! Spin around safely once and shake your hands in the air!",
            emoji = "💃",
            durationSeconds = 10,
            rewardPoints = 20,
            color = SleekGold,
            colorDark = SleekGoldDark
        )
    )

    fun getRandomQuest(excludeId: String? = null): PhysicalBreakQuest {
        val filtered = if (excludeId != null) allQuests.filter { it.id != excludeId } else allQuests
        return filtered.randomOrNull() ?: allQuests.first()
    }
}
