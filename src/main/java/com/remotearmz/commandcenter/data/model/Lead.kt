package com.remotearmz.commandcenter.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leads")
data class Lead(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val company: String,
    val designation: String,
    val source: LeadSourceclass MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                MaterialTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainApp()
                    }
                }
            }
        }
    }<activity
        android:name=".MainActivity"
        android:exported="true"
        android:theme="@style/Theme.RemoteArmzCommandCenter">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>,
    val status: LeadStatus,
    val notes: String,
    val createdAt: Long,
    val updatedAt: Long
)

enum class LeadSource {
    WEBSITE,
    REFERRAL,
    SOCIAL_MEDIA,
    EMAIL_CAMPAIGN,
    OTHER
}

enum class LeadStatus {
    NEW,
    CONTACTED,
    QUALIFIED,
    PROPOSAL_SENT,
    NEGOTIATING,
    WON,
    LOST
}
