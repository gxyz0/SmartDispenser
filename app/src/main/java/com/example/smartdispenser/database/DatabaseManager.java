package com.example.smartdispenser.database;

import android.content.Context;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private Context context;
    private static Connection connection = null;

    public DatabaseManager(Context context) {
        this.context = context;
    }

    // 单例模式 返回MyDatabaseManager
    private static DatabaseManager INSTANCE;

    public static synchronized DatabaseManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DatabaseManager(context);
        }
        return INSTANCE;
    }

    public void startConnection() {
        //要连接的数据库url
        String url = "jdbc:mysql://113.45.149.252:3306/smartdispenser?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false";
        //连接数据库使用的用户名
        String userName = "root";
        //连接数据库使用的密码
        String password = "#Abc123456";
        try {
            //加载驱动
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            //获取与数据库的连接
            connection = DriverManager.getConnection(url, userName, password);
            System.out.println("数据库连接成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 在程序结束时关闭数据库连接
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // insert插入user表
    public void insertUser(User user) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (connection != null) {
            try {
                // sql语句
                String sql = "INSERT INTO user (user_name, user_password) VALUES (?, ?)";
                // 获取用于向数据库发送sql语句的ps
                ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUserName());
                ps.setString(2, user.getUserPassword());
                ps.execute();
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    user.setUserId(userId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // update更新user表
    public void updateUser(User user) {
        PreparedStatement ps = null;
        if (connection != null) {
            try {
                // sql语句
                String sql = "UPDATE user SET user_name=?, user_password=? WHERE user_id=?";
                // 获取用于向数据库发送sql语句的ps
                ps = connection.prepareStatement(sql);
                ps.setString(1, user.getUserName());
                ps.setString(2, user.getUserPassword());
                ps.setInt(3, user.getUserId());
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // get获取user表
    public User getUser(int userId) {
        User user = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (connection != null) {
            try {
                // sql语句
                String sql = "SELECT * FROM user WHERE user_id = ?";
                // 获取用于向数据库发送sql语句的ps
                ps = connection.prepareStatement(sql);
                ps.setInt(1, userId);
                rs = ps.executeQuery();
                // 处理查询结果
                if (rs.next()) {
                    String userName = rs.getString("user_name");
                    String userPassword = rs.getString("user_password");
                    user = new User(userId, userName, userPassword);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return user;
    }

    // 根据userName获取userId
    public int getUserIdByName(String userName) {
        int userId = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (connection != null) {
            try {
                // sql语句
                String sql = "SELECT * FROM user WHERE user_name = ?";
                // 获取用于向数据库发送sql语句的ps
                ps = connection.prepareStatement(sql);
                ps.setString(1, userName);
                rs = ps.executeQuery();
                // 处理查询结果
                if (rs.next()) {
                    userId = rs.getInt("user_id");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return userId;
    }

    // 根据userName和userPassword获取userId
    public int getUserIdByNamePassword(String userName, String userPassword) {
        int userId = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (connection != null) {
            try {
                // sql语句
                String sql = "SELECT * FROM user WHERE user_name = ? AND user_password = ?";
                // 获取用于向数据库发送sql语句的ps
                ps = connection.prepareStatement(sql);
                ps.setString(1, userName);
                ps.setString(2, userPassword);
                rs = ps.executeQuery();
                // 处理查询结果
                if (rs.next()) {
                    userId = rs.getInt("user_id");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return userId;
    }

    // insert插入userInfo表
    public void insertUserInfo(UserInfo userinfo) {
        PreparedStatement ps = null;
        if (connection != null) {
            try {
                // sql语句
                String sql = "INSERT INTO userInfo (user_id, user_nickname, user_phone, user_email, user_gender, user_birthday, user_health_status, user_image) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                // 获取用于向数据库发送sql语句的ps
                ps = connection.prepareStatement(sql);
                ps.setInt(1, userinfo.getUserId());
                ps.setString(2, userinfo.getUserNickname());
                ps.setString(3, userinfo.getUserPhone());
                ps.setString(4, userinfo.getUserEmail());
                ps.setString(5, userinfo.getUserGender());
                ps.setString(6, userinfo.getUserBirthday());
                ps.setString(7, userinfo.getUserHealthStatus());
                ps.setString(8, userinfo.getUserImage());
                ps.execute();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // update更新userInfo表
    public void updateUserInfo(UserInfo userinfo) {
        PreparedStatement ps = null;
        if (connection != null) {
            try {
                // sql语句
                String sql = "UPDATE userInfo SET user_nickname=?, user_phone=?, user_email=?, user_gender=?, user_birthday=?, user_health_status=?, user_image=? WHERE user_id=?";
                // 获取用于向数据库发送sql语句的ps
                ps = connection.prepareStatement(sql);
                ps.setString(1, userinfo.getUserNickname());
                ps.setString(2, userinfo.getUserPhone());
                ps.setString(3, userinfo.getUserEmail());
                ps.setString(4, userinfo.getUserGender());
                ps.setString(5, userinfo.getUserBirthday());
                ps.setString(6, userinfo.getUserHealthStatus());
                ps.setString(7, userinfo.getUserImage());
                ps.setInt(8, userinfo.getUserId());
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // get获取userInfo表
    public UserInfo getUserInfo(int userId) {
        UserInfo userInfo = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (connection != null) {
            try {
                // sql语句
                String sql = "SELECT * FROM userInfo WHERE user_id = ?";
                // 获取用于向数据库发送sql语句的ps
                ps = connection.prepareStatement(sql);
                ps.setInt(1, userId);
                rs = ps.executeQuery();
                // 处理查询结果
                if (rs.next()) {
                    String userNickname = rs.getString("user_nickname");
                    String userPhone = rs.getString("user_phone");
                    String userEmail = rs.getString("user_email");
                    String userGender = rs.getString("user_gender");
                    String userBirthday = rs.getString("user_birthday");
                    String userHealthStatus = rs.getString("user_health_status");
                    String userImage = rs.getString("user_image");
                    userInfo = new UserInfo(userId, userNickname, userPhone, userEmail, userGender, userBirthday, userHealthStatus, userImage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return userInfo;
    }


    // insert插入medication表
    public void insertMedication(Medication medication) {
        PreparedStatement ps = null;
        if (connection != null) {
            try {
                // sql语句
                String sql = "INSERT INTO medication (medication_id, user_id, medication_name, medication_quantity, medication_note, medication_image) VALUES (?, ?, ?, ?, ?, ?)";
                // 获取用于向数据库发送sql语句的ps
                ps = connection.prepareStatement(sql);
                ps.setInt(1, medication.getMedicationId());
                ps.setInt(2, medication.getUserId());
                ps.setString(3, medication.getMedicationName());
                ps.setInt(4, medication.getMedicationQuantity());
                ps.setString(5, medication.getMedicationNote());
                ps.setString(6, medication.getMedicationImage());
                ps.execute();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // update更新medication表
    public void updateMedication(Medication medication) {
        PreparedStatement ps = null;
        if (connection != null) {
            try {
                // sql语句
                String sql = "UPDATE medication SET medication_id=?, user_id=?, medication_name=?, medication_quantity=?, medication_note=?, medication_image=? WHERE id=?";
                // 获取用于向数据库发送sql语句的ps
                ps = connection.prepareStatement(sql);
                ps.setInt(1, medication.getMedicationId());
                ps.setInt(2, medication.getUserId());
                ps.setString(3, medication.getMedicationName());
                ps.setInt(4, medication.getMedicationQuantity());
                ps.setString(5, medication.getMedicationNote());
                ps.setString(6, medication.getMedicationImage());
                ps.setInt(7, medication.getId());
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // get获取medication表
    public List<Medication> getMedication(int userId) {
        List<Medication> medicationList = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (connection != null) {
            try {
                // sql语句
                String sql = "SELECT * FROM Medication WHERE user_id = ? ORDER BY medication_id ASC";
                // 获取用于向数据库发送sql语句的ps
                ps = connection.prepareStatement(sql);
                ps.setInt(1, userId);
                rs = ps.executeQuery();
                // 处理查询结果
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int medicationId = rs.getInt("medication_id");
                    String medicationName = rs.getString("medication_name");
                    int medicationQuantity = rs.getInt("medication_quantity");
                    String medicationNote = rs.getString("medication_note");
                    String medicationImage = rs.getString("medication_image");
                    Medication medication = new Medication(id, medicationId, userId, medicationName, medicationQuantity, medicationNote, medicationImage);
                    medicationList.add(medication);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return medicationList;
    }


    // insert插入reminder表
    public void insertReminder(Reminder reminder) {
        PreparedStatement ps = null;
        if (connection != null) {
            try {
                // sql语句
                String sql = "INSERT INTO reminder (reminder_id, user_id, reminder_name, medication_id, medication_taking_num, reminder_start_date, reminder_end_date, reminder_time, reminder_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                // 获取用于向数据库发送sql语句的ps
                ps = connection.prepareStatement(sql);
                ps.setInt(1, reminder.getReminderId());
                ps.setInt(2, reminder.getUserId());
                ps.setString(3, reminder.getReminderName());
                ps.setInt(4, reminder.getMedicationId());
                ps.setInt(5, reminder.getMedicationTakingNum());
                ps.setString(6, reminder.getReminderStartDate());
                ps.setString(7, reminder.getReminderEndDate());
                ps.setString(8, reminder.getReminderTime());
                ps.setBoolean(9, reminder.getReminderActive());
                ps.execute();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // delete删除reminder表
    public void deleteReminder(Reminder reminder) {
        PreparedStatement ps = null;
        if (connection != null) {
            try {
                // SQL语句
                String sql = "DELETE FROM reminder WHERE id = ?";
                // 获取用于向数据库发送sql语句的ps
                ps = connection.prepareStatement(sql);
                ps.setInt(1, reminder.getId());
                // 执行删除操作
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("成功删除了 " + affectedRows + " 条提醒。");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // update更新reminder表
    public void updateReminder(Reminder reminder) {
        PreparedStatement ps = null;
        if (connection != null) {
            try {
                // sql语句
                String sql = "UPDATE reminder SET reminder_id=?, user_id=?, reminder_name=?, medication_id=?, medication_taking_num=?, reminder_start_date=?, reminder_end_date=?, reminder_time=?, reminder_active=? WHERE id=?";
                // 获取用于向数据库发送sql语句的ps
                ps = connection.prepareStatement(sql);
                ps.setInt(1, reminder.getReminderId());
                ps.setInt(2, reminder.getUserId());
                ps.setString(3, reminder.getReminderName());
                ps.setInt(4, reminder.getMedicationId());
                ps.setInt(5, reminder.getMedicationTakingNum());
                ps.setString(6, reminder.getReminderStartDate());
                ps.setString(7, reminder.getReminderEndDate());
                ps.setString(8, reminder.getReminderTime());
                ps.setBoolean(9, reminder.getReminderActive());
                ps.setInt(10, reminder.getId());
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // get获取reminder表
    public List<Reminder> getReminder(int userId) {
        List<Reminder> reminderList = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (connection != null) {
            try {
                // sql语句
                String sql = "SELECT * FROM reminder WHERE user_id = ? ORDER BY reminder_id ASC";
                // 获取用于向数据库发送sql语句的ps
                ps = connection.prepareStatement(sql);
                ps.setInt(1, userId);
                rs = ps.executeQuery();
                // 处理查询结果
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int reminderId = rs.getInt("reminder_id");
                    String reminderName = rs.getString("reminder_name");
                    int medicationId = rs.getInt("medication_id");
                    int medicationTakingNum = rs.getInt("medication_taking_num");
                    String reminderStartDate = rs.getString("reminder_start_date");
                    String reminderEndDate = rs.getString("reminder_end_date");
                    String reminderTime = rs.getString("reminder_time");
                    boolean reminderActive = rs.getBoolean("reminder_active");
                    Reminder reminder = new Reminder(id, reminderId, userId, reminderName, medicationId, medicationTakingNum, reminderStartDate, reminderEndDate, reminderTime, reminderActive);
                    reminderList.add(reminder);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return reminderList;
    }


    // insert插入reminderDateTime表
    public void insertReminderDateTime(List<ReminderDateTime> reminderDateTimeList) {
        PreparedStatement ps = null;
        if (connection != null) {
            try {
                // sql语句
                String sql = "INSERT INTO reminderDateTime (user_id, medication_id, medication_taking_num, reminder_date_time, is_taking) VALUES (?, ?, ?, ?, ?)";
                // 获取用于向数据库发送sql语句的ps
                ps = connection.prepareStatement(sql);
                // 遍历 List 并插入每个元素
                for (ReminderDateTime reminderDateTime : reminderDateTimeList) {
                    // 设置参数
                    ps.setInt(1, reminderDateTime.getUserId());
                    ps.setInt(2, reminderDateTime.getMedicationId());
                    ps.setInt(3, reminderDateTime.getMedicationTakingNum());
                    ps.setString(4, reminderDateTime.getReminderDateTime());
                    ps.setBoolean(5, reminderDateTime.isTaking());
                    // 执行插入操作
                    ps.addBatch(); // 添加到批处理
                }
                ps.executeBatch(); // 执行批处理
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // delete删除reminderDateTime表
    public void deleteReminderDateTime(List<ReminderDateTime> reminderDateTimeList) {
        PreparedStatement ps = null;
        if (connection != null) {
            try {
                // SQL语句
                String sql = "DELETE FROM reminderDateTime WHERE user_id = ? AND medication_id = ? AND reminder_date_time = ?";
                // 获取用于向数据库发送sql语句的ps
                ps = connection.prepareStatement(sql);
                // 遍历 List 并删除每个元素
                for (ReminderDateTime reminderDateTime : reminderDateTimeList) {
                    // 设置参数
                    ps.setInt(1, reminderDateTime.getUserId());
                    ps.setInt(2, reminderDateTime.getMedicationId());
                    ps.setString(3, reminderDateTime.getReminderDateTime());
                    // 执行删除操作
                    ps.addBatch(); // 添加到批处理
                }
                ps.executeBatch(); // 执行批处理
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // update更新reminderDateTimeIsTaking
    public void updateReminderDateTimeIsTaking(ReminderDateTime reminderDateTime) {
        PreparedStatement ps = null;
        if (connection != null) {
            try {
                // sql语句
                String sql = "UPDATE reminderDateTime SET is_taking=? WHERE id=?";
                // 获取用于向数据库发送sql语句的ps
                ps = connection.prepareStatement(sql);
                ps.setBoolean(1, reminderDateTime.isTaking());
                ps.setInt(2, reminderDateTime.getId());
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // get获取reminder表
    public List<ReminderDateTime> getReminderDateTime(int userId) {
        List<ReminderDateTime> reminderDateTimeList = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        if (connection != null) {
            try {
                // sql语句
                String sql = "SELECT * FROM reminderDateTime WHERE user_id = ?";
                // 获取用于向数据库发送sql语句的ps
                ps = connection.prepareStatement(sql);
                ps.setInt(1, userId);
                rs = ps.executeQuery();
                // 处理查询结果
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int medicationId = rs.getInt("medication_id");
                    int medicationTakingNum = rs.getInt("medication_taking_num");
                    String reminderDateTimeStr = rs.getString("reminder_date_time");
                    boolean isTaking = rs.getBoolean("is_taking");
                    ReminderDateTime reminderDateTime = new ReminderDateTime(id, userId, medicationId, medicationTakingNum, reminderDateTimeStr, isTaking);
                    reminderDateTimeList.add(reminderDateTime);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return reminderDateTimeList;
    }
}