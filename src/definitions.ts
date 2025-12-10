export interface BackgroundSpeechPlugin {
  /**
   * Start wake word detection foreground service.
   * Requires RECORD_AUDIO and FOREGROUND_SERVICE permissions.
   * @since 1.0.0
   */
  start(): Promise<void>;

  /**
   * Stop wake word detection foreground service.
   * @since 1.0.0
   */
  stop(): Promise<void>;

  /**
   * Update the list of wake phrases to listen for.
   * @param options {phrases: string[]} An array of phrases.
   * @since 1.0.0
   */
  updateWakePhrases(options: { phrases: string[] }): Promise<void>;
}