import { WebPlugin } from '@capacitor/core';

import type { BackgroundSpeechPlugin } from './definitions';

export class BackgroundSpeechWeb extends WebPlugin implements BackgroundSpeechPlugin {
  async start(): Promise<void> {
    this.unavailable('Not available on web.');
  }

  async stop(): Promise<void> {
    this.unavailable('Not available on web.');
  }

  async updateWakePhrases(options: { phrases: string[] }): Promise<void> {
    console.warn('updateWakePhrases not available on web.', options.phrases);
    this.unavailable('Not available on web.');
  }
}