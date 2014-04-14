package models

import java.util.Date

case class EmailSetting(ownerEmail: String,
                        accountServerAddress: String,
                        accountUsername: String,
                        accountPassword: String,
                        latestCheckedReceivedDate: Date = new Date())
