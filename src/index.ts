import { registerPlugin } from '@capacitor/core';

import type { BackgroundSpeechPlugin } from './definitions';

const BackgroundSpeech = registerPlugin<BackgroundSpeechPlugin>('BackgroundSpeech', {
  web: () => import('./web').then((m) => new m.BackgroundSpeechWeb()),
});

export * from './definitions';
export { BackgroundSpeech };
