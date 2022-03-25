package com.team9.questgame.gamemanager.record.socket;

/**
 * {
 *    "title": string (Optional)    // Title of the notification, default will be the empty string
 *    "message": string             // custom message
 *    "imgSrc": string              // image source to show alongside the message
 *    "action": string              // (optional - TBD) the action that server wants the client to do
 * }
 */
public record NotificationOutbound(String title, String message, String imgSrc, String action) {
}
