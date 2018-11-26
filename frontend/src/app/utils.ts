export const parseNumber = function (value: string | number, defaultValue: number = 0): number {
  return !isNaN(Number(value.toString())) ? Number(value) : defaultValue;
};
