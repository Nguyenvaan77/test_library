/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Main;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Lenovo
 */
public class UserDashboard extends javax.swing.JFrame {
    public String AccountName;
    /**
     * Creates new form User_DEMO
     */
    public UserDashboard(String AccountName) {
        this.AccountName = AccountName;
        initComponents();
        Connect();
        UserGreeting_Load();
    }
    
    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    public void Connect(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
                    con = DriverManager.getConnection("jdbc:mysql://localhost:3308/" + Database.DB_Name,Database.DB_UserName,Database.DB_Password);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
    
//Load UserGreeting in
    private void UserGreeting_Load() {
//        Recent_Issue_Load();
        user_name.setText("Welcome, User " + AccountName);
        account_name.setText(AccountName);
        try {
            String sql_cmd = "SELECT Email,ContactNumber FROM accounts WHERE AccountName = ?";
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1,AccountName);
            rs = pst.executeQuery();
            rs.next();
            email.setText(rs.getString(1));
            contact_number.setText(rs.getString(2));
            Home_page_Load();
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//---------------------------------------------------------------------------------------------------------------------------------------------
  
//Initialize Home_page
    private void Home_page_Load() {
        Status_issueLoad();
        View_book_Load();
    }
//Load count of statsus issue
    private void Status_issueLoad(){
        int count_borrowing_book = 0;
        int count_returning_book = 0;
        int count_overtime_book = 0;
        String sql_cmd = "SELECT issuebooks.Status "
                + " FROM issuebooks "
                + " JOIN books ON issuebooks.Book_ID = books.Book_ID "
                + " JOIN accounts ON issuebooks.User_ID = accounts.AccountName "
                + " WHERE accounts.AccountName = ? ";
        try {
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1, AccountName);
            rs = pst.executeQuery();
            while(rs.next()){
                String status = rs.getString(1);
                if(status.equals("Borrowed") == true){
                    ++ count_borrowing_book;
                }
                else{
                    if(status.equals("Returned") == true){
                        ++ count_returning_book;
                    }
                    else{
                        if(status.equals("OverTime") == true){
                            ++ count_overtime_book;
                        }
                    }
                }
            }
            borrowedNumber.setText("   " + String.valueOf(count_borrowing_book));
            returnedNumber.setText("   " + String.valueOf(count_returning_book));
            overtimeNumber.setText("   " + String.valueOf(count_overtime_book));
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//Load recent_issue_table in Home_page
    private void View_book_Load() {
        String sql_cmd = "SELECT issuebooks.Book_ID,books.Name,issuebooks.IssueDate,issuebooks.DueDate,issuebooks.Status "
                + "FROM issuebooks "
                + " JOIN books ON issuebooks.Book_ID = books.Book_ID "
                + " JOIN accounts ON issuebooks.User_ID = accounts.AccountName "
                + "WHERE accounts.AccountName = ?";
        try {
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1, AccountName);
            rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel)view_book_jscroll.getModel();
            model.setNumRows(0);
            while(rs.next()) {
                Object[] obj = {rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5)};
                model.addRow(obj);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//---------------------------------------------------------------------------------------------------------------------------------------------
   
//Turn off all button
    private void TurnOffButtons() {
        Color color = new Color(51,51,51);
        home_page_button.setBackground(color);
        edit_profile_button.setBackground(color);
        change_password_button.setBackground(color);
        logout_button.setBackground(color);
    }
//---------------------------------------------------------------------------------------------------------------------------------------------
    
//Initialize changepassword_panel
    private void Changepassword_panelLoad() {
        Changepassword_buttonLoad();
        Changepassword_TextandComboboxLoad();
    }   
//Load button_status when initialize category_panel
    private void Changepassword_buttonLoad() {
        Submit_button.setEnabled(true);
    }
//Load text and combobox status when initialize category_panel
    private void Changepassword_TextandComboboxLoad() {
        txtpassword.setText("");
        txtpassword1.setText("");
        txtpassword2.setText("");
    }
//Change password where condition is pass
    private void Change_password_Databases(String newPass){
        String sql_cmd = "UPDATE accounts SET PassWord = ? WHERE AccountName = ?";
        try {
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1,newPass);
            pst.setString(2,AccountName);
            pst.executeUpdate(); 
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//Function help checking change_password
    private void Change_password(String correctCurPass,String curPass, String newPass, String confirmPass){
        if(correctCurPass.equals("") == true || correctCurPass.equals("") == true || correctCurPass.equals("") == true || correctCurPass.equals("") == true){
            JOptionPane.showMessageDialog(this, "Phan nhap mat khau khong duoc de trong");
        }
        else{
            if(correctCurPass.equals(curPass) == false){
                JOptionPane.showMessageDialog(this, "IncorrectPassword");
            }
            else{
                if(newPass.equals(confirmPass) == false){
                    JOptionPane.showMessageDialog(this, "newPass and confirmPass is not Same");
                }
                else{
                    if(curPass.equals(confirmPass) == true){
                        JOptionPane.showMessageDialog(this, "mat khau cu phai khac mat khau moi");
                    }
                    else{
                        JOptionPane.showMessageDialog(this, "Submit successfully");
                        Change_password_Databases(newPass);
                    }
                }
            }
        }
    }
//---------------------------------------------------------------------------------------------------------------------------------------------
    
//Initialize edit profile panel
    private void Editprofile_panelLoad(){
        Editprofile_buttonLoad();
        Editprofile_TextandComboboxLoad();
    }
//Load button_status when initialize  edit profile panel
    private void Editprofile_buttonLoad() {
        editprofile_saveButton.setEnabled(true);
        editprofile_resetButton.setEnabled(true);
    } 
//Load text and combobox status when initialize edit profile panel
    private void Editprofile_TextandComboboxLoad() {
        account_name_label.setText(AccountName);
        try {
                String sql_cmd = "SELECT ContactNumber,Email FROM accounts WHERE AccountName = ? ";
                pst = con.prepareStatement(sql_cmd);
                pst.setString(1,AccountName);
                rs = pst.executeQuery();
                rs.next();
                contact_number_label.setText(rs.getString(1));
                email_label.setText(rs.getString(2));
                
                contact_number_label.setText(rs.getString(1));
                email_label.setPlaceholder(rs.getString(2));
            } catch (SQLException ex) {
                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }   
    }
//*****************************************************************
//Check a string Email is a valid Email.
    private boolean check_validEmail(String email){
        int indexFirst = email.indexOf('@');
        if(email.substring(indexFirst).equals("@gmail.com") == false || Character.isLetter(email.charAt(0)) == false || indexFirst == -1 || email.length() < 10){
            return false;
        }
        else{
            return true;
        }
    }
//Check a string contact number is a valid number.
    private boolean check_validContactNumber(String contactNumber){
        for(int i=0;i<contactNumber.length();++i){
            if(Character.isDigit(contactNumber.charAt(i)) == false){
                return false;
            }
        }
        return true;
    }
//Change CONTACT NUMBER indatabases when edit profile
    private void change_contactNumber_DB(String old_contactNumber, String new_contactNumber){
        if(check_validContactNumber(new_contactNumber) == false || old_contactNumber.equals(new_contactNumber) == true || new_contactNumber.equals("") == true) return;
        else{
            try {
                String sql_cmd = "UPDATE accounts SET ContactNumber = ? WHERE AccountName = ? ";
                pst = con.prepareStatement(sql_cmd);
                pst.setString(1,new_contactNumber);
                pst.setString(2,AccountName);
                pst.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }
//Change EMAIL in databases when edit profile
    private void change_email_DB(String old_mail, String new_email){
        if(check_validEmail(new_email) == false || old_mail.equals(new_email) == true) return;
        else{
            try {
                String sql_cmd = "UPDATE accounts SET Email = ? WHERE AccountName = ? ";
                pst = con.prepareStatement(sql_cmd);
                pst.setString(1,new_email);
                pst.setString(2,AccountName);
                pst.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }
//Check and edit information
    private boolean check_and_edit_information(String old_contactNumber, String new_contactNumber, String old_email, String new_email){
        if(new_contactNumber.equals("") == true || new_email.equals("") == true || check_validEmail(new_email) == false || check_validContactNumber(new_contactNumber) == false) return false;
        else{
            change_email_DB(old_email,new_email);
            change_contactNumber_DB(old_contactNumber,new_contactNumber);
            return true;
        }
    }
    
//---------------------------------------------------------------------------------------------------------------------------------------------
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        user_name = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        edit_profile_button = new javax.swing.JButton();
        home_page_button = new javax.swing.JButton();
        logout_button = new javax.swing.JButton();
        change_password_button = new javax.swing.JButton();
        Parent_panel = new javax.swing.JPanel();
        home_page_panel = new javax.swing.JPanel();
        JPanel = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        returnedNumber = new javax.swing.JLabel();
        JLabel = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        borrowedNumber = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        overtimeNumber = new javax.swing.JLabel();
        j = new javax.swing.JLabel();
        Jlabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        view_book_jscroll = new rojeru_san.complementos.RSTableMetro();
        jPanel4 = new javax.swing.JPanel();
        icon_member = new javax.swing.JLabel();
        member_infor_label = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        account_name = new javax.swing.JLabel();
        contact_number = new javax.swing.JLabel();
        email = new javax.swing.JLabel();
        changepassword_panel = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        txtpassword = new app.bolivia.swing.JCTextField();
        txtpassword1 = new app.bolivia.swing.JCTextField();
        txtpassword2 = new app.bolivia.swing.JCTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        Submit_button = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        editprofile_panel = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        editprofile_saveButton = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        contact_number_label = new app.bolivia.swing.JCTextField();
        email_label = new app.bolivia.swing.JCTextField();
        jPanel10 = new javax.swing.JPanel();
        account_name_label = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        editprofile_resetButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(73, 101, 128));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/menu_50px.png"))); // NOI18N
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 50, 50));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 0, -1, 50));

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI", 1, 20)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Library Management System");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 310, 30));

        user_name.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        user_name.setForeground(new java.awt.Color(255, 255, 255));
        user_name.setText("Welcome, User ");
        jPanel2.add(user_name, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 10, 150, 30));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/member_off_off_50px.png"))); // NOI18N
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 0, -1, -1));

        jLabel6.setFont(new java.awt.Font("Tw Cen MT", 1, 36)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/cancel_red_50px.png"))); // NOI18N
        jLabel6.setText("X");
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
        });
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(1090, 10, 40, 30));

        jPanel3.setBackground(new java.awt.Color(73, 101, 128));
        jPanel3.setForeground(new java.awt.Color(51, 51, 51));

        edit_profile_button.setBackground(new java.awt.Color(255, 219, 150));
        edit_profile_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        edit_profile_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/change_info_26px.png"))); // NOI18N
        edit_profile_button.setText("    Edit Profile");
        edit_profile_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        edit_profile_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_profile_buttonActionPerformed(evt);
            }
        });

        home_page_button.setBackground(new java.awt.Color(255, 219, 150));
        home_page_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        home_page_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/home_color_24px.png"))); // NOI18N
        home_page_button.setText("   Home Page");
        home_page_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        home_page_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                home_page_buttonActionPerformed(evt);
            }
        });

        logout_button.setBackground(new java.awt.Color(255, 219, 150));
        logout_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        logout_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Exit_26px.png"))); // NOI18N
        logout_button.setText("        Logout");
        logout_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        logout_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logout_buttonActionPerformed(evt);
            }
        });

        change_password_button.setBackground(new java.awt.Color(255, 219, 150));
        change_password_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        change_password_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/change_pass_off_50.png"))); // NOI18N
        change_password_button.setText("Change Password");
        change_password_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        change_password_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                change_password_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 19, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(edit_profile_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(home_page_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(change_password_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(logout_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 19, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(home_page_button, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(edit_profile_button, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addComponent(change_password_button, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(logout_button, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(211, Short.MAX_VALUE))
        );

        Parent_panel.setLayout(new java.awt.CardLayout());

        home_page_panel.setBackground(new java.awt.Color(186, 221, 255));
        home_page_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        JPanel.setBackground(new java.awt.Color(186, 221, 255));

        jPanel8.setBackground(new java.awt.Color(186, 221, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(255, 0, 51)));

        returnedNumber.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        returnedNumber.setForeground(new java.awt.Color(102, 102, 102));
        returnedNumber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/return_book_50px.png"))); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(returnedNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(returnedNumber)
                .addGap(26, 26, 26))
        );

        JLabel.setBackground(new java.awt.Color(186, 221, 255));
        JLabel.setFont(new java.awt.Font("Segoe UI", 3, 20)); // NOI18N
        JLabel.setText("Số sách trong hạn");

        jPanel9.setBackground(new java.awt.Color(186, 221, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(255, 0, 51)));

        borrowedNumber.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        borrowedNumber.setForeground(new java.awt.Color(102, 102, 102));
        borrowedNumber.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        borrowedNumber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/book_50px.png"))); // NOI18N

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(borrowedNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addComponent(borrowedNumber)
                .addGap(26, 26, 26))
        );

        jPanel11.setBackground(new java.awt.Color(186, 221, 255));
        jPanel11.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(255, 0, 51)));

        overtimeNumber.setBackground(new java.awt.Color(186, 221, 255));
        overtimeNumber.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        overtimeNumber.setForeground(new java.awt.Color(102, 102, 102));
        overtimeNumber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/over_time_50px.png"))); // NOI18N

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(overtimeNumber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(overtimeNumber)
                .addGap(16, 16, 16))
        );

        j.setBackground(new java.awt.Color(186, 221, 255));
        j.setFont(new java.awt.Font("Segoe UI", 3, 20)); // NOI18N
        j.setText("Sách quá hạn");

        Jlabel.setBackground(new java.awt.Color(186, 221, 255));
        Jlabel.setFont(new java.awt.Font("Segoe UI", 3, 20)); // NOI18N
        Jlabel.setText("Số sách đã trả");

        view_book_jscroll.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "BookID", "BookName", "NgayMuon", "NgayTra", "TrangThai"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        view_book_jscroll.setColorFilasBackgound2(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(view_book_jscroll);

        jPanel4.setBackground(new java.awt.Color(186, 221, 255));

        icon_member.setForeground(new java.awt.Color(102, 102, 102));
        icon_member.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/member_off_50px.png"))); // NOI18N

        member_infor_label.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        member_infor_label.setText("Member Information");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel3.setText("AccountName :");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel4.setText("ContactNumber :");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel10.setText("Email :");

        account_name.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        account_name.setText("_accountname_");

        contact_number.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        contact_number.setText("_contactmnumber_");

        email.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        email.setText("_email_");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(icon_member)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(member_infor_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(62, 62, 62))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(contact_number, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(account_name, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(email, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(6, 6, 6))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(icon_member)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(member_infor_label, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(account_name))
                .addGap(17, 17, 17)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(contact_number))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(email)
                    .addComponent(jLabel10))
                .addGap(17, 17, 17))
        );

        javax.swing.GroupLayout JPanelLayout = new javax.swing.GroupLayout(JPanel);
        JPanel.setLayout(JPanelLayout);
        JPanelLayout.setHorizontalGroup(
            JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(JLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Jlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(j, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(JPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 876, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 31, Short.MAX_VALUE))
        );
        JPanelLayout.setVerticalGroup(
            JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPanelLayout.createSequentialGroup()
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(JPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(Jlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(j, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(JLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE))
        );

        home_page_panel.add(JPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 880, 640));

        Parent_panel.add(home_page_panel, "card2");

        changepassword_panel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/SplashIcon/quenmatkhau.png"))); // NOI18N

        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/SplashIcon/change.png"))); // NOI18N

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        txtpassword.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtpassword.setPlaceholder("Enter Password .....");
        txtpassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtpasswordActionPerformed(evt);
            }
        });

        txtpassword1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtpassword1.setPlaceholder("Enter New Password");
        txtpassword1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtpassword1ActionPerformed(evt);
            }
        });

        txtpassword2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtpassword2.setPlaceholder("Confirm Password");
        txtpassword2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtpassword2ActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel14.setText("Current password");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(223, 25, 25));
        jLabel15.setText("* ");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel16.setText("Confirm password");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(223, 25, 25));
        jLabel17.setText("* ");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel18.setText("New password");

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(223, 25, 25));
        jLabel19.setText("* ");

        Submit_button.setBackground(new java.awt.Color(127, 186, 0));
        Submit_button.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        Submit_button.setForeground(new java.awt.Color(255, 255, 255));
        Submit_button.setText("Submit");
        Submit_button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        Submit_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Submit_buttonActionPerformed(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel28.setText("Change password");

        jLabel29.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(121, 121, 121));
        jLabel29.setText("Update password for enhanced account security");

        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/reset_pass_50px.png"))); // NOI18N

        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/password_icon_50px.png"))); // NOI18N

        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/key_50px.png"))); // NOI18N

        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/idea_50px.png"))); // NOI18N

        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/confirm_50px.png"))); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel32, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel33, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jLabel18)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jLabel16)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txtpassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtpassword1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtpassword2, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel29)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jLabel28)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel31))))))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(Submit_button, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel30)
                        .addGap(48, 48, 48))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(jLabel28))
                            .addComponent(jLabel31))
                        .addGap(8, 8, 8)
                        .addComponent(jLabel29)
                        .addGap(43, 43, 43)))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtpassword, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtpassword1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtpassword2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(Submit_button, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout changepassword_panelLayout = new javax.swing.GroupLayout(changepassword_panel);
        changepassword_panel.setLayout(changepassword_panelLayout);
        changepassword_panelLayout.setHorizontalGroup(
            changepassword_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(changepassword_panelLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(changepassword_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        changepassword_panelLayout.setVerticalGroup(
            changepassword_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(changepassword_panelLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel20)
                .addGap(66, 66, 66))
            .addGroup(changepassword_panelLayout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(87, Short.MAX_VALUE))
        );

        Parent_panel.add(changepassword_panel, "card3");

        editprofile_panel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setText("AccountName ( Can't change ) : ");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setText("ContactNumber : ");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setText("Email : ");

        editprofile_saveButton.setBackground(new java.awt.Color(127, 186, 0));
        editprofile_saveButton.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        editprofile_saveButton.setForeground(new java.awt.Color(255, 255, 255));
        editprofile_saveButton.setText("Save");
        editprofile_saveButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        editprofile_saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editprofile_saveButtonActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel13.setText("Edit profile");

        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/email_50px.png"))); // NOI18N

        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/member_off_off_50px.png"))); // NOI18N

        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/call_50px.png"))); // NOI18N

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/SplashIcon/edit_pro5_64px.png"))); // NOI18N

        jLabel26.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(121, 121, 121));
        jLabel26.setText("Update contactnumber and email for better security");

        contact_number_label.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        contact_number_label.setPlaceholder("Contact Number . . .");

        email_label.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        email_label.setPlaceholder("Email . . . ");

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        account_name_label.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        account_name_label.setText("_AccountName_");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(account_name_label)
                .addContainerGap(252, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(account_name_label, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/SplashIcon/boyreadingbook_490.png"))); // NOI18N
        jLabel35.setText("jLabel35");

        editprofile_resetButton.setBackground(new java.awt.Color(127, 186, 0));
        editprofile_resetButton.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        editprofile_resetButton.setForeground(new java.awt.Color(255, 255, 255));
        editprofile_resetButton.setText("Reset");
        editprofile_resetButton.setActionCommand("Reset\n");
        editprofile_resetButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        editprofile_resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editprofile_resetButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel24)
                    .addComponent(jLabel25)
                    .addComponent(jLabel23))
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jLabel26)))
                    .addComponent(jLabel9)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(email_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel12Layout.createSequentialGroup()
                                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel12)
                                            .addComponent(jLabel11)
                                            .addComponent(contact_number_label, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(4, 4, 4)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(editprofile_saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(editprofile_resetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(48, 48, 48)))
                        .addComponent(jLabel35)))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel26))
                    .addComponent(jLabel8))
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(contact_number_label, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel23)
                            .addComponent(email_label, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(48, 48, 48)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(editprofile_saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(editprofile_resetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 72, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel35)))
                .addContainerGap())
        );

        javax.swing.GroupLayout editprofile_panelLayout = new javax.swing.GroupLayout(editprofile_panel);
        editprofile_panel.setLayout(editprofile_panelLayout);
        editprofile_panelLayout.setHorizontalGroup(
            editprofile_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        editprofile_panelLayout.setVerticalGroup(
            editprofile_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editprofile_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        Parent_panel.add(editprofile_panel, "card4");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1140, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addGap(0, 267, Short.MAX_VALUE)
                    .addComponent(Parent_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 873, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addGap(0, 56, Short.MAX_VALUE)
                    .addComponent(Parent_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 668, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jLabel6MouseClicked

    private void edit_profile_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_profile_buttonActionPerformed
        // TODO add your handling code here:
        Parent_panel.removeAll();
        Parent_panel.add(editprofile_panel);
        Parent_panel.repaint();
        Parent_panel.revalidate();
        Editprofile_panelLoad();
        TurnOffButtons();
        edit_profile_button.setBackground(new Color(255,0,51));
    }//GEN-LAST:event_edit_profile_buttonActionPerformed

    private void home_page_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_home_page_buttonActionPerformed
        // TODO add your handling code here:
        Parent_panel.removeAll();
        Parent_panel.add(home_page_panel);
        Parent_panel.repaint();
        Parent_panel.revalidate();
        Home_page_Load();
        TurnOffButtons();
        home_page_button.setBackground(new Color(255,0,51));
    }//GEN-LAST:event_home_page_buttonActionPerformed

    private void logout_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logout_buttonActionPerformed
        // TODO add your handling code here:
        this.dispose();
        new Login().setVisible(true);
    }//GEN-LAST:event_logout_buttonActionPerformed

    private void change_password_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_change_password_buttonActionPerformed
        // TODO add your handling code here:
        Parent_panel.removeAll();
        Parent_panel.add(changepassword_panel);
        Parent_panel.repaint();
        Parent_panel.revalidate();
        Changepassword_panelLoad();
        TurnOffButtons();
        change_password_button.setBackground(new Color(255,0,51));
    }//GEN-LAST:event_change_password_buttonActionPerformed

    private void txtpasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtpasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtpasswordActionPerformed

    private void txtpassword1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtpassword1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtpassword1ActionPerformed

    private void txtpassword2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtpassword2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtpassword2ActionPerformed

    private void Submit_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Submit_buttonActionPerformed
        // TODO add your handling code here:
        String curPass = txtpassword.getText();
        String newPass = txtpassword1.getText();
        String confirmPass = txtpassword2.getText();
        
        try {
            String sql_cmd;
            sql_cmd = "SELECT PassWord FROM accounts WHERE AccountName = ? ";
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1,AccountName);
            rs = pst.executeQuery();
            rs.next();
            String correctPass = rs.getString(1);
            Change_password(correctPass,curPass,newPass,confirmPass);
            
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Submit_buttonActionPerformed

    private void editprofile_saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editprofile_saveButtonActionPerformed
        // TODO add your handling code here:
        String old_contact = null;
        String old_email = null;
        try {
            String sql_cmd;
            sql_cmd = "SELECT ContactNumber,Email FROM accounts WHERE AccountName = ? ";
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1,AccountName);
            rs = pst.executeQuery();
            rs.next();
            old_contact = rs.getString(1);
            old_email = rs.getString(2);
            if(check_and_edit_information(old_contact,contact_number_label.getText(),old_email,email_label.getText())){
                JOptionPane.showMessageDialog(this, "Edit successfully");
            }
            else{
                JOptionPane.showMessageDialog(this, "Error");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_editprofile_saveButtonActionPerformed

    private void editprofile_resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editprofile_resetButtonActionPerformed
        // TODO add your handling code here:
        String old_contact = null;
        String old_email = null;
        try {
            String sql_cmd;
            sql_cmd = "SELECT ContactNumber,Email FROM accounts WHERE AccountName = ? ";
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1,AccountName);
            rs = pst.executeQuery();
            rs.next();
            old_contact = rs.getString(1);
            old_email = rs.getString(2);
            contact_number_label.setText(old_contact);
            email_label.setText(old_email);
            contact_number_label.setPlaceholder(old_contact);
            email_label.setPlaceholder(old_email);
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_editprofile_resetButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UserDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UserDashboard("user").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel JLabel;
    private javax.swing.JPanel JPanel;
    private javax.swing.JLabel Jlabel;
    private javax.swing.JPanel Parent_panel;
    private javax.swing.JButton Submit_button;
    private javax.swing.JLabel account_name;
    private javax.swing.JLabel account_name_label;
    private javax.swing.JLabel borrowedNumber;
    private javax.swing.JButton change_password_button;
    private javax.swing.JPanel changepassword_panel;
    private javax.swing.JLabel contact_number;
    private app.bolivia.swing.JCTextField contact_number_label;
    private javax.swing.JButton edit_profile_button;
    private javax.swing.JPanel editprofile_panel;
    private javax.swing.JButton editprofile_resetButton;
    private javax.swing.JButton editprofile_saveButton;
    private javax.swing.JLabel email;
    private app.bolivia.swing.JCTextField email_label;
    private javax.swing.JButton home_page_button;
    private javax.swing.JPanel home_page_panel;
    private javax.swing.JLabel icon_member;
    private javax.swing.JLabel j;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton logout_button;
    private javax.swing.JLabel member_infor_label;
    private javax.swing.JLabel overtimeNumber;
    private javax.swing.JLabel returnedNumber;
    private app.bolivia.swing.JCTextField txtpassword;
    private app.bolivia.swing.JCTextField txtpassword1;
    private app.bolivia.swing.JCTextField txtpassword2;
    private javax.swing.JLabel user_name;
    private rojeru_san.complementos.RSTableMetro view_book_jscroll;
    // End of variables declaration//GEN-END:variables
}
