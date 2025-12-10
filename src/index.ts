import { registerPlugin } from '@capacitor/core';

import type { MimosaPlugin } from './definitions';

const Mimosa = registerPlugin<MimosaPlugin>('Mimosa', {
  web: () => import('./web').then((m) => new m.MimosaWeb()),
});

export * from './definitions';
export { Mimosa };
