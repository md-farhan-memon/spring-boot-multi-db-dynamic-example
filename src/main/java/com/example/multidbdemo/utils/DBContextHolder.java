package com.example.multidbdemo.utils;

public class DBContextHolder {
//   private static final ThreadLocal<ClientNames> contextHolder = new ThreadLocal<>();

//   public static void setCurrentDb(ClientNames dbType) {
//       contextHolder.set(dbType);
//   }
//   public static ClientNames getCurrentDb() {
//       return contextHolder.get();
//   }
//   public static void clear() {
//       contextHolder.remove();
//   }

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

  public static void setCurrentDb(String dbType) {
      contextHolder.set(dbType);
  }
  public static String getCurrentDb() {
      return contextHolder.get();
  }
  public static void clear() {
      contextHolder.remove();
  }
}
