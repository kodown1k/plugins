import { WebPlugin } from '@capacitor/core';

import type { MimosaPlugin } from './definitions';

export class MimosaWeb extends WebPlugin implements MimosaPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
