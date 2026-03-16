
export const storage = {
  clear() {
    localStorage.clear();
  },
  get(key: string) {
    const value = localStorage.getItem(key);
    try {
      return value ? JSON.parse(value) : null;
    } catch {
      return value;
    }
  },
  remove(key: string) {
    localStorage.removeItem(key);
  },
  set(key: string, value: any) {
    localStorage.setItem(key, JSON.stringify(value));
  }
};
