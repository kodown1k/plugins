export interface MimosaPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
