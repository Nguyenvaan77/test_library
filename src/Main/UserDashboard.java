/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Main;

import com.github.lgooddatepicker.components.DatePickerSettings;
import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int NO_SELECTION = -1;
    class Table_status {
        public int Previous_book_selected;
        public Table_status() {
            this.Previous_book_selected = NO_SELECTION;
        }        
    }
    Table_status table_status = new Table_status();
    /**
     * Creates new form User_DEMO
     */
    public UserDashboard(String AccountName) {
        this.AccountName = AccountName;
        this.con = Database.getInstance().getConnection();
        initComponents();
        UserGreeting_Load();
    }
    
    Connection con;
    PreparedStatement pst;
    ResultSet rs;
//    public void Connect(){
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//                con = DriverManager.getConnection("jdbc:mysql://localhost:3308/" + Database.DB_Name,Database.DB_UserName,Database.DB_Password);
//        } catch (ClassNotFoundException | SQLException ex) {
//            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    } 
    
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
                + " JOIN accounts ON issuebooks.UserName = accounts.AccountName "
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
                + " JOIN accounts ON issuebooks.UserName = accounts.AccountName "
                + "WHERE accounts.AccountName = ? AND issuebooks.Status NOT IN (?) ";
        try {
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1, AccountName);
            pst.setString(2, "Returned");
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
////Initialize Book_panel
    private void Book_panelLoad() {
//        book_combobox_check.reset();
        
        Book_TableLoad();
        Book_ComboboxDataLoad();
        Book_buttonLoad();
        Book_TextandComboboxLoad();
    }       
//Load button_status when initialize AdminDashboard
    private void Book_buttonLoad() {
        book_reviewButton.setEnabled(true);
        book_searchButton.setEnabled(false);
        if(book_table.getSelectedRow() >= 0){
            book_borrowButton.setEnabled(true);
        }
        else{
            book_borrowButton.setEnabled(false);
        }
//        book_category.setEnabled(false);
//        book_author.setEnabled(false);
//        book_publisher.setEnabled(false);
    }
//Load text and combobox status when initialize AdminDashboard
    private void Book_TextandComboboxLoad() {
        book_id_search.setSelectedIndex(0);
        book_name.setText("");
        book_quantity.setText("");
        book_category.setSelectedIndex(-1);
        book_author.setSelectedIndex(-1);
        book_publisher.setSelectedIndex(-1);
        book_category_search.setSelectedIndex(0);
        book_author_search.setSelectedIndex(0);
        book_publisher_search.setSelectedIndex(0);        
    }
// Load publisher_table in Book_panel
    private void Book_TableLoad(){
            table_status.Previous_book_selected = NO_SELECTION;
            try {
                pst = con.prepareStatement("SELECT b.Book_ID, b.Name,c.Category_ID ,c.Name as Category,a.Author_ID,a.Name as Author,p.Publisher_ID,p.Name as Publisher, b.Quantity FROM `books`b "
                        + "JOIN categories c ON b.Category_ID = c.Category_ID "
                        + "JOIN authors a ON b.Author_ID = a.Author_ID "
                        + "JOIN publishers p ON b.Publisher_ID = p.Publisher_ID Order By b.Book_ID ASC");
                rs = pst.executeQuery();
                int columns = 6;
                DefaultTableModel model = (DefaultTableModel)book_table.getModel();
                model.setRowCount(0);
                while(rs.next()){
                    Object[] obj = new Object[columns];
                    obj[0] = rs.getString("Book_ID");
                    obj[1] = rs.getString("Name");
                    obj[2] = new CategoryItem(rs.getInt("Category_ID"), rs.getString("Category"));
                    obj[3] = new AuthorItem(rs.getInt("Author_ID"), rs.getString("Author"));
                    obj[4] = new PublisherItem(rs.getInt("Publisher_ID"), rs.getString("Publisher"));
                    obj[5] = rs.getString("Quantity");
                    model.addRow(obj);
                }

            } catch (SQLException ex) {
                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }       
        }
//Load publisher id into publisher_id combobox
    private void Book_ComboboxDataLoad(){
        try {
            book_id_search.removeAllItems();        
            book_category.removeAllItems();
            book_author.removeAllItems();
            book_publisher.removeAllItems();
            book_category_search.removeAllItems();
            book_author_search.removeAllItems();
            book_publisher_search.removeAllItems();            
            pst = con.prepareStatement("Select Book_ID from Books b");
            book_id_search.addItem("All");
//            book_combobox_check.book_id_check = true;
            rs = pst.executeQuery();
            while(rs.next()){
                book_id_search.addItem(rs.getString("Book_ID"));
            }
            //Load book_category
            pst = con.prepareStatement("SELECT Category_ID, Name FROM categories");
            rs = pst.executeQuery();
            book_category_search.addItem(new CategoryItem(-1,"All"));
//            book_combobox_check.book_category_check = true;
            while(rs.next()){
                book_category.addItem(new CategoryItem(rs.getInt("Category_ID"), rs.getString("Name")));
                book_category_search.addItem(new CategoryItem(rs.getInt("Category_ID"), rs.getString("Name")));
            }
            //Load book_author
            pst = con.prepareStatement("SELECT Author_ID, Name FROM authors");
            rs = pst.executeQuery();
            book_author_search.addItem(new AuthorItem(-1,"All"));
//            book_combobox_check.book_author_check = true;
            while(rs.next()){
                book_author.addItem(new AuthorItem(rs.getInt("Author_ID"), rs.getString("Name")));
                book_author_search.addItem(new AuthorItem(rs.getInt("Author_ID"), rs.getString("Name")));
            }
            //Load book_publisher
            pst = con.prepareStatement("SELECT Publisher_ID, Name FROM publishers");
            rs = pst.executeQuery();
            book_publisher_search.addItem(new PublisherItem(-1,"All"));
//            book_combobox_check.book_publisher_check = true;
            while(rs.next()){
                book_publisher.addItem(new PublisherItem(rs.getInt("Publisher_ID"),rs.getString("Name")));
                book_publisher_search.addItem(new PublisherItem(rs.getInt("Publisher_ID"),rs.getString("Name")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }           
    } 
//---------------------------------------------------------------------------------------------------------------------------------------------     
//Turn off all button
    private void TurnOffButtons() {
        Color color = new Color(255,219,150);
        home_page_button.setBackground(color);
        edit_profile_button.setBackground(color);
        change_password_button.setBackground(color);
        myBookShelf_button.setBackground(color);
        goToLibrary_button.setBackground(color);
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
            JOptionPane.showMessageDialog(this, "The password field cannot be left empty");
        }
        else{
            if(correctCurPass.equals(curPass) == false){
                JOptionPane.showMessageDialog(this, "IncorrectPassword");
            }
            else{
                if(newPass.equals(confirmPass) == false){
                    JOptionPane.showMessageDialog(this, "newPass and confirmPass is not the same");
                }
                else{
                    if(curPass.equals(confirmPass) == true){
                        JOptionPane.showMessageDialog(this, "Old password must differ from new password");
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
//***************************************************
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
//  0. Load greetingSearchPanel
    private void SearchBook_greetingLoad(){
        id_text.setText("");
        bookName_text.setText("");
        ngayMuon_date.setDate(LocalDate.of(2000,1,1));
        ngayTra_date.setDate(LocalDate.of(2100,12,31));
        trangThai_cbbox.setSelectedIndex(0);
    }
//Initialize BOOK SHELF - SEARCH BOOK
    private void Searchbook_panelLoad() {
        SearchBook_settingFormat();
        SearchBook_panel_Load();
        SearchBook_view_bookJSC_Load();
    }
//  1.1 Load All Setting format
    private void SearchBook_settingFormat() {
        DatePickerSettings dateSettings1 = new DatePickerSettings();// Khởi tạo 1 biến setting
        DatePickerSettings dateSettings2 = new DatePickerSettings();// Khởi tạo 1 biến setting
        dateSettings1.setFormatForDatesCommonEra("dd/MM/yyyy");     // Đặt định dạng cho DatePicker
        dateSettings2.setFormatForDatesCommonEra("dd/MM/yyyy");     // Đặt định dạng cho DatePicker
        ngayMuon_date.setSettings(dateSettings1);
        ngayTra_date.setSettings(dateSettings2);
    }
//  1.2 Load buttons, text and comboBox of Search panel
    private void SearchBook_panel_Load() {
        SearchBook_buttonLoad();
        SearchBook_textLoad();
    }
//  1.3 Load jscrool which review book's information
    private void SearchBook_view_bookJSC_Load() {
        String sql_cmd = " SELECT issuebooks.Book_ID,books.Name,issuebooks.IssueDate,issuebooks.DueDate,issuebooks.Status "
                + " FROM issuebooks "
                + " JOIN books ON issuebooks.Book_ID = books.Book_ID "
                + " JOIN accounts ON issuebooks.UserName = accounts.AccountName "
                + " WHERE accounts.AccountName = ? AND issuebooks.Book_ID LIKE ? AND books.Name LIKE ? AND issuebooks.IssueDate >= ? AND issuebooks.DueDate <= ? ";
        String cur_Status = (String) trangThai_cbbox.getSelectedItem();
        if ((cur_Status).equals("All") == false) {
            String extraString = " AND issuebooks.Status = ? ";
            sql_cmd += extraString;
        }
        else{
            String extraString = " AND issuebooks.Status NOT IN (?) ";
            sql_cmd += extraString;
        }
        try {
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1, AccountName);
            pst.setString(2, "%" + id_text.getText() + "%");
            pst.setString(3, "%" + bookName_text.getText() + "%");
            pst.setString(4, String.format("%d-%d-%d", ngayMuon_date.getDate().getYear(), ngayMuon_date.getDate().getMonthValue(), ngayMuon_date.getDate().getDayOfMonth()));
            pst.setString(5, String.format("%d-%d-%d", ngayTra_date.getDate().getYear(), ngayTra_date.getDate().getMonthValue(), ngayTra_date.getDate().getDayOfMonth()));
            if ((cur_Status).equals("All") == false) {
                pst.setString(6, cur_Status);
            }
            else{
                pst.setString(6,"Returned");
            }
            rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel) view_book_search_jscroll.getModel();
            model.setNumRows(0);
            while (rs.next()) {
                Object[] obj = {rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)};
                model.addRow(obj);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//  1.2.1 Load buttons of search panel
    private void SearchBook_buttonLoad() {
        traSach_button.setEnabled(true);
        timKiem_button.setEnabled(true);
        toanBo_button.setEnabled(true);
    }

//  1.2.2 Load text od search panel
    private void SearchBook_textLoad() {
    }
//******************************************************************************
    //  *** Return Book 
//  1. Check What Row is chosen
    private int indexOfRow() {// trả về thứ tự của dòng trong bảng bắt đầu từ 0, nếu không có dòng nào được chọn thì trả về - 1
        return view_book_search_jscroll.getSelectedRow();
    }
    //  2. Return Book
    private boolean returnBook() {
        int indexBook = indexOfRow();

        if (indexOfRow() == -1 || view_book_search_jscroll.getValueAt(indexBook, 4).equals("Returned")) {
            return false;
        }

        String sql_cmd_UpdateStatus = "UPDATE issuebooks SET issuebooks.Status = ? , issuebooks.ActualDueDate = ? WHERE issuebooks.Book_ID = ? ";
        String sql_cmd_UpdateCount = "UPDATE books SET books.Quantity = books.Quantity + 1 WHERE books.Book_ID = ? ";
        PreparedStatement pstStatus;
        PreparedStatement pstCount;
        try {
            con.setAutoCommit(false);
            pstStatus = con.prepareStatement(sql_cmd_UpdateStatus);
            pstStatus.setString(1, "Returned");
            pstStatus.setString(2, LocalDateTime.now().format(formatter));
            pstStatus.setString(3, (String) view_book_search_jscroll.getValueAt(indexBook, 0));
            int countChangeStatus = pstStatus.executeUpdate();
            
            pstCount = con.prepareStatement(sql_cmd_UpdateCount);
            pstCount.setString(1,view_book_search_jscroll.getValueAt(indexBook,0).toString());
            int countChangeCount = pstCount.executeUpdate();
            
            con.commit();
            
            return countChangeStatus > 0 && countChangeCount > 0;
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            try{
                con.setAutoCommit(true);
            } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
//*****************************************************************************
//Check valid Date -- Condition : ngayMuon >= ngayTra && ngayMuon >= 01/01/2000 && ngayTra <= 31/12/2100
    private boolean Check_searchDate() {
        if (ngayMuon_date.getDate().isBefore(LocalDate.of(2000, 01, 01)) == true || ngayTra_date.getDate().isAfter(LocalDate.of(2100, 12, 31)) == true || ngayMuon_date.getDate().isAfter(ngayTra_date.getDate()) == true) {
            return false;
        } else return true;
    }
//--------------------------------------------------------------------------------------------------------------------------------------
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
        myBookShelf_button = new javax.swing.JButton();
        goToLibrary_button = new javax.swing.JButton();
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
        jLabel7 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        searchbook_panel = new javax.swing.JPanel();
        view_search_book_jscroll = new javax.swing.JScrollPane();
        view_book_search_jscroll = new rojeru_san.complementos.RSTableMetro();
        search_panel = new javax.swing.JPanel();
        tim_kiem_panel = new javax.swing.JLabel();
        book_id_label = new javax.swing.JLabel();
        book_name_label = new javax.swing.JLabel();
        trang_thai_panel = new javax.swing.JLabel();
        id_text = new app.bolivia.swing.JCTextField();
        bookName_text = new app.bolivia.swing.JCTextField();
        ngayMuon_label = new javax.swing.JLabel();
        ngayTra_label = new javax.swing.JLabel();
        ngayTra_date = new com.github.lgooddatepicker.components.DatePicker();
        ngayMuon_date = new com.github.lgooddatepicker.components.DatePicker();
        timKiem_button = new javax.swing.JButton();
        traSach_button = new javax.swing.JButton();
        trangThai_cbbox = new javax.swing.JComboBox<>();
        toanBo_button = new javax.swing.JButton();
        jLabel36 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        book_panel = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        book_table = new rojeru_san.complementos.RSTableMetro();
        jPanel6 = new javax.swing.JPanel();
        book_id_search = new javax.swing.JComboBox();
        book_name = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        book_author = new javax.swing.JComboBox<>();
        book_publisher = new javax.swing.JComboBox<>();
        book_category = new javax.swing.JComboBox<>();
        book_quantity = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        book_searchButton = new javax.swing.JButton();
        book_id = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        book_category_search = new javax.swing.JComboBox();
        jLabel42 = new javax.swing.JLabel();
        book_author_search = new javax.swing.JComboBox();
        book_publisher_search = new javax.swing.JComboBox();
        jLabel43 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        book_reviewButton = new javax.swing.JButton();
        book_borrowButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

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

        user_name.setText("Welcome, User ");
        user_name.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        user_name.setForeground(new java.awt.Color(255, 255, 255));
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

        edit_profile_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/change_off_50.png"))); // NOI18N
        edit_profile_button.setText("    Edit Profile");
        edit_profile_button.setBackground(new java.awt.Color(255, 219, 150));
        edit_profile_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        edit_profile_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        edit_profile_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_profile_buttonActionPerformed(evt);
            }
        });

        home_page_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/home_50px.png"))); // NOI18N
        home_page_button.setText("   Home Page");
        home_page_button.setBackground(new java.awt.Color(255, 219, 150));
        home_page_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        home_page_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        home_page_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                home_page_buttonActionPerformed(evt);
            }
        });

        logout_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/logout_50px.png"))); // NOI18N
        logout_button.setText("     Logout");
        logout_button.setBackground(new java.awt.Color(255, 219, 150));
        logout_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        logout_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        logout_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logout_buttonActionPerformed(evt);
            }
        });

        change_password_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/change_pass_50px.png"))); // NOI18N
        change_password_button.setText("Change Password");
        change_password_button.setBackground(new java.awt.Color(255, 219, 150));
        change_password_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        change_password_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        change_password_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                change_password_buttonActionPerformed(evt);
            }
        });

        myBookShelf_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/book_shelf_50px.png"))); // NOI18N
        myBookShelf_button.setText("My BookShelf");
        myBookShelf_button.setBackground(new java.awt.Color(255, 219, 150));
        myBookShelf_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        myBookShelf_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        myBookShelf_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                myBookShelf_buttonActionPerformed(evt);
            }
        });

        goToLibrary_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/library_2_50px.png"))); // NOI18N
        goToLibrary_button.setText("Go to library");
        goToLibrary_button.setBackground(new java.awt.Color(255, 219, 150));
        goToLibrary_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        goToLibrary_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        goToLibrary_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goToLibrary_buttonActionPerformed(evt);
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
                    .addComponent(logout_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(myBookShelf_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(goToLibrary_button, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 19, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(home_page_button, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(edit_profile_button, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(change_password_button, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(myBookShelf_button, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(goToLibrary_button, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(logout_button, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        Parent_panel.setLayout(new java.awt.CardLayout());

        home_page_panel.setBackground(new java.awt.Color(186, 221, 255));
        home_page_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        JPanel.setBackground(new java.awt.Color(186, 221, 255));

        jPanel8.setBackground(new java.awt.Color(186, 221, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(255, 0, 51)));

        returnedNumber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/return_book_50px.png"))); // NOI18N
        returnedNumber.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        returnedNumber.setForeground(new java.awt.Color(102, 102, 102));

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

        JLabel.setText("Sách đang mượn");
        JLabel.setBackground(new java.awt.Color(186, 221, 255));
        JLabel.setFont(new java.awt.Font("Segoe UI", 3, 20)); // NOI18N

        jPanel9.setBackground(new java.awt.Color(186, 221, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(255, 0, 51)));

        borrowedNumber.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        borrowedNumber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/book_50px.png"))); // NOI18N
        borrowedNumber.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        borrowedNumber.setForeground(new java.awt.Color(102, 102, 102));

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

        overtimeNumber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/over_time_50px.png"))); // NOI18N
        overtimeNumber.setBackground(new java.awt.Color(186, 221, 255));
        overtimeNumber.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        overtimeNumber.setForeground(new java.awt.Color(102, 102, 102));

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

        j.setText("Sách quá hạn");
        j.setBackground(new java.awt.Color(186, 221, 255));
        j.setFont(new java.awt.Font("Segoe UI", 3, 20)); // NOI18N

        Jlabel.setText("Sách đã trả");
        Jlabel.setBackground(new java.awt.Color(186, 221, 255));
        Jlabel.setFont(new java.awt.Font("Segoe UI", 3, 20)); // NOI18N

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
        view_book_jscroll.setRowHeight(50);
        view_book_jscroll.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        view_book_jscroll.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(view_book_jscroll);
        if (view_book_jscroll.getColumnModel().getColumnCount() > 0) {
            view_book_jscroll.getColumnModel().getColumn(0).setResizable(false);
            view_book_jscroll.getColumnModel().getColumn(1).setResizable(false);
            view_book_jscroll.getColumnModel().getColumn(2).setResizable(false);
            view_book_jscroll.getColumnModel().getColumn(3).setResizable(false);
            view_book_jscroll.getColumnModel().getColumn(4).setResizable(false);
        }

        jPanel4.setBackground(new java.awt.Color(186, 221, 255));

        icon_member.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/member_off_50px.png"))); // NOI18N
        icon_member.setForeground(new java.awt.Color(102, 102, 102));

        member_infor_label.setText("Member Information");
        member_infor_label.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N

        jLabel3.setText("AccountName :");
        jLabel3.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N

        jLabel4.setText("ContactNumber :");
        jLabel4.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N

        jLabel10.setText("Email :");
        jLabel10.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N

        account_name.setText("_accountname_");
        account_name.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        contact_number.setText("_contactmnumber_");
        contact_number.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        email.setText("_email_");
        email.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

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
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE))
        );

        home_page_panel.add(JPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 880, 640));

        Parent_panel.add(home_page_panel, "card2");

        changepassword_panel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/SplashIcon/quenmatkhau.png"))); // NOI18N

        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/SplashIcon/change.png"))); // NOI18N

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        txtpassword.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtpassword.setPlaceholder("Enter Password .....");

        txtpassword1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtpassword1.setPlaceholder("Enter New Password");

        txtpassword2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtpassword2.setPlaceholder("Confirm Password");

        jLabel14.setText("Current password");
        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel15.setText("* ");
        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(223, 25, 25));

        jLabel16.setText("Confirm password");
        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel17.setText("* ");
        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(223, 25, 25));

        jLabel18.setText("New password");
        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel19.setText("* ");
        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(223, 25, 25));

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

        jLabel28.setText("Change password");
        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N

        jLabel29.setText("Update password for enhanced account security");
        jLabel29.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(121, 121, 121));

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

        jLabel9.setText("AccountName ( Can't change ) : ");
        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel11.setText("ContactNumber : ");
        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jLabel12.setText("Email : ");
        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

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

        jLabel13.setText("Edit profile");
        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N

        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/email_50px.png"))); // NOI18N

        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/member_off_off_50px.png"))); // NOI18N

        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/call_50px.png"))); // NOI18N

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/SplashIcon/edit_pro5_64px.png"))); // NOI18N

        jLabel26.setText("Update contactnumber and email for better security");
        jLabel26.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(121, 121, 121));

        contact_number_label.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        contact_number_label.setPlaceholder("Contact Number . . .");

        email_label.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        email_label.setPlaceholder("Email . . . ");

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        account_name_label.setText("_AccountName_");
        account_name_label.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(account_name_label)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(account_name_label, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/SplashIcon/boyreadingbook_490.png"))); // NOI18N

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

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/SplashIcon/girl_120.png"))); // NOI18N

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/idea_girl_50px.png"))); // NOI18N

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
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel26)))
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21)
                        .addGap(155, 155, 155))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel12Layout.createSequentialGroup()
                                    .addGap(19, 19, 19)
                                    .addComponent(editprofile_saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(48, 48, 48)
                                    .addComponent(editprofile_resetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(email_label, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(contact_number_label, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE))))
                        .addGap(16, 16, 16)
                        .addComponent(jLabel35)
                        .addGap(12, 12, 12))))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel26))
                            .addComponent(jLabel8))
                        .addGap(46, 46, 46)
                        .addComponent(jLabel9))
                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel21)
                        .addComponent(jLabel7)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(contact_number_label, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel12)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel23)
                                    .addComponent(email_label, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addGap(97, 97, 97)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(editprofile_resetButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(editprofile_saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addComponent(jLabel35))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        searchbook_panel.setBackground(new java.awt.Color(186, 221, 255));

        view_book_search_jscroll.setModel(new javax.swing.table.DefaultTableModel(
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
        view_book_search_jscroll.setColorFilasBackgound2(new java.awt.Color(255, 204, 153));
        view_book_search_jscroll.setRowHeight(50);
        view_book_search_jscroll.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        view_book_search_jscroll.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                view_book_search_jscrollMouseClicked(evt);
            }
        });
        view_search_book_jscroll.setViewportView(view_book_search_jscroll);

        search_panel.setBackground(new java.awt.Color(255, 255, 255));

        tim_kiem_panel.setText("Search");
        tim_kiem_panel.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N

        book_id_label.setText("Book_ID :");
        book_id_label.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        book_name_label.setText("Book_Name :");
        book_name_label.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        trang_thai_panel.setText("TrangThai :");
        trang_thai_panel.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        id_text.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 112, 192), 2));
        id_text.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        id_text.setPlaceholder("ID . . .");

        bookName_text.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 112, 192), 2));
        bookName_text.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        bookName_text.setPlaceholder("Name . . .");

        ngayMuon_label.setText("IssueDate :");
        ngayMuon_label.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        ngayTra_label.setText("DueDate :");
        ngayTra_label.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        ngayTra_date.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        ngayMuon_date.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        timKiem_button.setText("Search");
        timKiem_button.setBackground(new java.awt.Color(0, 112, 192));
        timKiem_button.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        timKiem_button.setForeground(new java.awt.Color(255, 255, 255));
        timKiem_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timKiem_buttonActionPerformed(evt);
            }
        });

        traSach_button.setText("Return ");
        traSach_button.setBackground(new java.awt.Color(0, 112, 192));
        traSach_button.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        traSach_button.setForeground(new java.awt.Color(255, 255, 255));
        traSach_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                traSach_buttonActionPerformed(evt);
            }
        });

        trangThai_cbbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Borrowed", "OverTime", "" }));
        trangThai_cbbox.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 112, 192), 2));
        trangThai_cbbox.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        toanBo_button.setText("All books");
        toanBo_button.setBackground(new java.awt.Color(0, 112, 192));
        toanBo_button.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        toanBo_button.setForeground(new java.awt.Color(255, 255, 255));
        toanBo_button.setToolTipText("");
        toanBo_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toanBo_buttonActionPerformed(evt);
            }
        });

        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/search_50px.png"))); // NOI18N

        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/book_search_50px.png"))); // NOI18N

        javax.swing.GroupLayout search_panelLayout = new javax.swing.GroupLayout(search_panel);
        search_panel.setLayout(search_panelLayout);
        search_panelLayout.setHorizontalGroup(
            search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(search_panelLayout.createSequentialGroup()
                .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(search_panelLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel36))
                    .addGroup(search_panelLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(tim_kiem_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(13, 13, 13)
                .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(book_id_label, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(trang_thai_panel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(book_name_label, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(search_panelLayout.createSequentialGroup()
                        .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(bookName_text, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(trangThai_cbbox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(search_panelLayout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(ngayTra_label)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(ngayTra_date, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(search_panelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(toanBo_button, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(timKiem_button, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(traSach_button, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, search_panelLayout.createSequentialGroup()
                        .addComponent(id_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(ngayMuon_label)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ngayMuon_date, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        search_panelLayout.setVerticalGroup(
            search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, search_panelLayout.createSequentialGroup()
                .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(search_panelLayout.createSequentialGroup()
                        .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(search_panelLayout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(book_id_label)
                                    .addComponent(id_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(search_panelLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(tim_kiem_panel)))
                        .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(search_panelLayout.createSequentialGroup()
                                .addGap(18, 65, Short.MAX_VALUE)
                                .addComponent(jLabel22))
                            .addGroup(search_panelLayout.createSequentialGroup()
                                .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(search_panelLayout.createSequentialGroup()
                                        .addGap(27, 27, 27)
                                        .addComponent(jLabel36))
                                    .addGroup(search_panelLayout.createSequentialGroup()
                                        .addGap(20, 20, 20)
                                        .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(bookName_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(book_name_label))))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(search_panelLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ngayMuon_date, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ngayMuon_label))
                        .addGap(33, 33, 33)
                        .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ngayTra_date, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ngayTra_label))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(search_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(trangThai_cbbox, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(trang_thai_panel)
                            .addComponent(toanBo_button, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(timKiem_button, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(traSach_button, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout searchbook_panelLayout = new javax.swing.GroupLayout(searchbook_panel);
        searchbook_panel.setLayout(searchbook_panelLayout);
        searchbook_panelLayout.setHorizontalGroup(
            searchbook_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(view_search_book_jscroll)
            .addComponent(search_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        searchbook_panelLayout.setVerticalGroup(
            searchbook_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, searchbook_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(search_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(view_search_book_jscroll, javax.swing.GroupLayout.PREFERRED_SIZE, 448, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        Parent_panel.add(searchbook_panel, "card5");

        book_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Book_ID", "Name", "Category", "Author", "Publisher", "Quantity"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        book_table.setColorFilasBackgound2(new java.awt.Color(204, 255, 51));
        book_table.setMultipleSeleccion(false);
        book_table.getTableHeader().setReorderingAllowed(false);
        book_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                book_tableMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(book_table);

        jPanel6.setBackground(new java.awt.Color(255, 51, 102));

        book_id_search.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                book_id_searchItemStateChanged(evt);
            }
        });

        jLabel37.setText("Name");

        book_author.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                book_authorItemStateChanged(evt);
            }
        });

        book_publisher.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                book_publisherItemStateChanged(evt);
            }
        });

        book_category.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                book_categoryItemStateChanged(evt);
            }
        });

        jLabel38.setText("Quantity");

        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("Info");
        jLabel39.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N

        jLabel66.setText("ID");

        jLabel67.setText("Category");

        jLabel68.setText("Author");

        jLabel69.setText("Publisher");

        book_searchButton.setText("SEARCH");
        book_searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                book_searchButtonActionPerformed(evt);
            }
        });

        book_id.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        book_id.setEnabled(false);

        jLabel40.setText("ID");

        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel41.setText("Search");
        jLabel41.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N

        jLabel42.setText("Category");

        jLabel43.setText("Author");

        jLabel70.setText("Publisher");

        book_reviewButton.setText("REVIEW");
        book_reviewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                book_reviewButtonActionPerformed(evt);
            }
        });

        book_borrowButton.setText("BORROW");
        book_borrowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                book_borrowButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                            .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                            .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel67, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel68, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel69, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(book_quantity)
                            .addComponent(book_name)
                            .addComponent(book_category, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(book_author, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(book_publisher, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                                .addComponent(book_reviewButton, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(book_borrowButton, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(book_id, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(book_searchButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(book_id_search, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel43, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel70, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel42, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(book_category_search, 0, 147, Short.MAX_VALUE)
                                    .addComponent(book_author_search, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(book_publisher_search, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(290, 290, 290)
                        .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(210, 210, 210))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel67)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(book_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel37)
                            .addComponent(book_id_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel40))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(book_quantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel38)
                            .addComponent(book_category_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel42))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel66)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(book_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(book_author_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel43)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(book_category, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(book_publisher_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel70)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel68)
                    .addComponent(book_author, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel69)
                    .addComponent(book_publisher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(book_reviewButton)
                    .addComponent(book_searchButton)
                    .addComponent(book_borrowButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout book_panelLayout = new javax.swing.GroupLayout(book_panel);
        book_panel.setLayout(book_panelLayout);
        book_panelLayout.setHorizontalGroup(
            book_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(book_panelLayout.createSequentialGroup()
                .addComponent(jScrollPane7)
                .addContainerGap())
        );
        book_panelLayout.setVerticalGroup(
            book_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, book_panelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        Parent_panel.add(book_panel, "card7");

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
        setLocationRelativeTo(null);
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
                contact_number_label.setText(old_contact);
                email_label.setText(old_email);
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

    private void timKiem_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timKiem_buttonActionPerformed
        // TODO add your handling code here:
        if (!Check_searchDate()) {
            JOptionPane.showMessageDialog(this, "Error in Date");
        } else {
            Searchbook_panelLoad();
        }
    }//GEN-LAST:event_timKiem_buttonActionPerformed

    private void traSach_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_traSach_buttonActionPerformed
        // TODO add your handling code here:
        if(!returnBook()){
            JOptionPane.showMessageDialog(this,"Index = -1 OR this book is returned");
        }
        else JOptionPane.showMessageDialog(this,"Return successfully");
        Searchbook_panelLoad();
    }//GEN-LAST:event_traSach_buttonActionPerformed

    private void toanBo_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toanBo_buttonActionPerformed
        // TODO add your handling code here:
        SearchBook_greetingLoad();
        Searchbook_panelLoad();
    }//GEN-LAST:event_toanBo_buttonActionPerformed

    private void myBookShelf_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_myBookShelf_buttonActionPerformed
        // TODO add your handling code here:
        Parent_panel.removeAll();
        Parent_panel.add(searchbook_panel);
        Parent_panel.repaint();
        Parent_panel.revalidate();
        SearchBook_greetingLoad();
        Searchbook_panelLoad();
        TurnOffButtons();
        myBookShelf_button.setBackground(new Color(255,0,51));
    }//GEN-LAST:event_myBookShelf_buttonActionPerformed

    private void goToLibrary_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goToLibrary_buttonActionPerformed
        // TODO add your handling code here:
        // TODO add your handling code here:
        Parent_panel.removeAll();
        Parent_panel.add(book_panel);
        Parent_panel.repaint();
        Parent_panel.revalidate();
        Book_panelLoad();
        TurnOffButtons();
        goToLibrary_button.setBackground(new Color(255,0,51));
    }//GEN-LAST:event_goToLibrary_buttonActionPerformed

    private void book_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_book_tableMouseClicked
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel)book_table.getModel();
        int clickedRow = book_table.rowAtPoint(evt.getPoint());
        if(clickedRow == NO_SELECTION) {
            book_borrowButton.setEnabled(false);
            book_reviewButton.setEnabled(false);
            book_searchButton.setEnabled(true);
            return;
        }
        table_status.Previous_book_selected = clickedRow;
        int selectIndex = book_table.getSelectedRow();
        //        book_id.setSelectedItem(model.getValueAt(selectIndex,0).toString());
        book_id.setText(model.getValueAt(selectIndex, 0).toString());
        book_name.setText(model.getValueAt(selectIndex,1).toString());
        book_category.setSelectedItem((CategoryItem)model.getValueAt(selectIndex,2));
        book_author.setSelectedItem((AuthorItem)model.getValueAt(selectIndex,3));
        book_publisher.setSelectedItem((PublisherItem)model.getValueAt(selectIndex,4));
        book_quantity.setText(model.getValueAt(selectIndex,5).toString());
        if(Integer.parseInt(model.getValueAt(selectIndex,5).toString()) <= 0){
            book_borrowButton.setEnabled(false);
        }
        else {
            book_borrowButton.setEnabled(true);
        }
        book_searchButton.setEnabled(true);
        book_reviewButton.setEnabled(true);
    }//GEN-LAST:event_book_tableMouseClicked

    private void book_id_searchItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_book_id_searchItemStateChanged
        //         TODO add your handling code here:
        //        if(evt.getStateChange() == ItemEvent.SELECTED){
            //            if(!book_combobox_check.book_id_check || !book_combobox_check.book_category_check
                //                || !book_combobox_check.book_author_check || !book_combobox_check.book_publisher_check){
                //                return;
                //            }
            //                String baseQuery = "SELECT b.Book_ID, b.Name,c.Category_ID ,c.Name as Category,a.Author_ID,a.Name as Author,p.Publisher_ID,p.Name as Publisher, b.Quantity "
            //                    + "FROM `books`b JOIN categories c ON b.Category_ID = c.Category_ID "
            //                    + "JOIN authors a ON b.Author_ID = a.Author_ID JOIN publishers p ON b.Publisher_ID = p.Publisher_ID WHERE 1=1";
            //            ArrayList<String> conditions = new ArrayList<>();
            //            ArrayList<Object> parameters = new ArrayList<>();
            //            boolean isID_All = true;
            //            boolean isCategory_All = true;
            //            boolean isAuthor_All = true;
            //            boolean isPublisher_All = true;
            //
            //            if(book_id.getSelectedIndex() != 0) isID_All = false;
            //            if(book_category.getSelectedIndex() != 0) isCategory_All = false;
            //            if(book_author.getSelectedIndex() != 0) isAuthor_All = false;
            //            if(book_publisher.getSelectedIndex() != 0) isPublisher_All = false;
            //
            //            if(!isID_All) {
                //                conditions.add("b.Book_ID = ? ");
                //                parameters.add(book_id.getSelectedItem().toString());
                //            }
            //            if(!isCategory_All) {
                //                conditions.add("c.Category_ID = ? ");
                //                CategoryItem ci = (CategoryItem)book_category.getSelectedItem();
                //                parameters.add(ci.getId());
                //            }
            //            if(!isAuthor_All) {
                //                conditions.add("a.Author_ID = ? ");
                //                AuthorItem ai = (AuthorItem)book_author.getSelectedItem();
                //                parameters.add(ai.getId());
                //            }
            //            if(!isPublisher_All) {
                //                conditions.add("p.Publisher_ID = ? ");
                //                PublisherItem pi = (PublisherItem)book_publisher.getSelectedItem();
                //                parameters.add(pi.getId());
                //            }
            //            for (String condition : conditions) {
                //                baseQuery += " AND " + condition;
                //            }
            //
            //            try {
                //                    PreparedStatement ps = con.prepareStatement(baseQuery);
                //                    for (int i = 0; i < parameters.size(); i++) {
                    //                    ps.setObject(i+1, parameters.get(i));
                    //                }
                //                    ResultSet r = ps.executeQuery();
                //
                //                    ResultSetMetaData rsd = r.getMetaData();
                //                    int columns = rsd.getColumnCount();
                //
                //                    DefaultTableModel model = (DefaultTableModel)book_table.getModel();
                //                    model.setRowCount(0);
                //
                //                    while(r.next()){
                    //                        Object[] obj = new Object[columns];
                    //                        obj[0] = r.getString("Book_ID");
                    //                        obj[1] = r.getString("Name");
                    //                        obj[2] = new CategoryItem(r.getInt("Category_ID"), r.getString("Category"));
                    //                        obj[3] = new AuthorItem(r.getInt("Author_ID"), r.getString("Author"));
                    //                        obj[4] = new PublisherItem(r.getInt("Publisher_ID"), r.getString("Publisher"));
                    //                        obj[5] = r.getString("Quantity");
                    //                        model.addRow(obj);
                    //                    }
                //
                //                } catch (SQLException ex) {
                //                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
                //            }
            //        }
    }//GEN-LAST:event_book_id_searchItemStateChanged

    private void book_authorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_book_authorItemStateChanged
        // TODO add your handling code here:
        //        if(evt.getStateChange() == ItemEvent.SELECTED){
            //            if(!book_combobox_check.book_id_check || !book_combobox_check.book_category_check
                //                || !book_combobox_check.book_author_check || !book_combobox_check.book_publisher_check){
                //                return;
                //            }
            //                String baseQuery = "SELECT b.Book_ID, b.Name,c.Category_ID ,c.Name as Category,a.Author_ID,a.Name as Author,p.Publisher_ID,p.Name as Publisher, b.Quantity "
            //                    + "FROM `books`b JOIN categories c ON b.Category_ID = c.Category_ID "
            //                    + "JOIN authors a ON b.Author_ID = a.Author_ID JOIN publishers p ON b.Publisher_ID = p.Publisher_ID WHERE 1=1";
            //            ArrayList<String> conditions = new ArrayList<>();
            //            ArrayList<Object> parameters = new ArrayList<>();
            //            boolean isID_All = true;
            //            boolean isCategory_All = true;
            //            boolean isAuthor_All = true;
            //            boolean isPublisher_All = true;
            //
            //            if(book_id.getSelectedIndex() != 0) isID_All = false;
            //            if(book_category.getSelectedIndex() != 0) isCategory_All = false;
            //            if(book_author.getSelectedIndex() != 0) isAuthor_All = false;
            //            if(book_publisher.getSelectedIndex() != 0) isPublisher_All = false;
            //
            //            if(!isID_All) {
                //                conditions.add("b.Book_ID = ? ");
                //                parameters.add(book_id.getSelectedItem().toString());
                //            }
            //            if(!isCategory_All) {
                //                conditions.add("c.Category_ID = ? ");
                //                CategoryItem ci = (CategoryItem)book_category.getSelectedItem();
                //                parameters.add(ci.getId());
                //            }
            //            if(!isAuthor_All) {
                //                conditions.add("a.Author_ID = ? ");
                //                AuthorItem ai = (AuthorItem)book_author.getSelectedItem();
                //                parameters.add(ai.getId());
                //            }
            //            if(!isPublisher_All) {
                //                conditions.add("p.Publisher_ID = ? ");
                //                PublisherItem pi = (PublisherItem)book_publisher.getSelectedItem();
                //                parameters.add(pi.getId());
                //            }
            //            for (String condition : conditions) {
                //                baseQuery += " AND " + condition;
                //            }
            //
            //            try {
                //                    PreparedStatement ps = con.prepareStatement(baseQuery);
                //                    for (int i = 0; i < parameters.size(); i++) {
                    //                    ps.setObject(i+1, parameters.get(i));
                    //                }
                //                    ResultSet r = ps.executeQuery();
                //
                //                    ResultSetMetaData rsd = r.getMetaData();
                //                    int columns = rsd.getColumnCount();
                //
                //                    DefaultTableModel model = (DefaultTableModel)book_table.getModel();
                //                    model.setRowCount(0);
                //
                //                    while(r.next()){
                    //                        Object[] obj = new Object[columns];
                    //                        obj[0] = r.getString("Book_ID");
                    //                        obj[1] = r.getString("Name");
                    //                        obj[2] = new CategoryItem(r.getInt("Category_ID"), r.getString("Category"));
                    //                        obj[3] = new AuthorItem(r.getInt("Author_ID"), r.getString("Author"));
                    //                        obj[4] = new PublisherItem(r.getInt("Publisher_ID"), r.getString("Publisher"));
                    //                        obj[5] = r.getString("Quantity");
                    //                        model.addRow(obj);
                    //                    }
                //
                //                } catch (SQLException ex) {
                //                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
                //            }
            //        }
    }//GEN-LAST:event_book_authorItemStateChanged

    private void book_publisherItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_book_publisherItemStateChanged
        // TODO add your handling code here:
        //        if(evt.getStateChange() == ItemEvent.SELECTED){
            //            if(!book_combobox_check.book_id_check || !book_combobox_check.book_category_check
                //                || !book_combobox_check.book_author_check || !book_combobox_check.book_publisher_check){
                //                return;
                //            }
            //                String baseQuery = "SELECT b.Book_ID, b.Name,c.Category_ID ,c.Name as Category,a.Author_ID,a.Name as Author,p.Publisher_ID,p.Name as Publisher, b.Quantity "
            //                    + "FROM `books`b JOIN categories c ON b.Category_ID = c.Category_ID "
            //                    + "JOIN authors a ON b.Author_ID = a.Author_ID JOIN publishers p ON b.Publisher_ID = p.Publisher_ID WHERE 1=1";
            //            ArrayList<String> conditions = new ArrayList<>();
            //            ArrayList<Object> parameters = new ArrayList<>();
            //            boolean isID_All = true;
            //            boolean isCategory_All = true;
            //            boolean isAuthor_All = true;
            //            boolean isPublisher_All = true;
            //
            //            if(book_id.getSelectedIndex() != 0) isID_All = false;
            //            if(book_category.getSelectedIndex() != 0) isCategory_All = false;
            //            if(book_author.getSelectedIndex() != 0) isAuthor_All = false;
            //            if(book_publisher.getSelectedIndex() != 0) isPublisher_All = false;
            //
            //            if(!isID_All) {
                //                conditions.add("b.Book_ID = ? ");
                //                parameters.add(book_id.getSelectedItem().toString());
                //            }
            //            if(!isCategory_All) {
                //                conditions.add("c.Category_ID = ? ");
                //                CategoryItem ci = (CategoryItem)book_category.getSelectedItem();
                //                parameters.add(ci.getId());
                //            }
            //            if(!isAuthor_All) {
                //                conditions.add("a.Author_ID = ? ");
                //                AuthorItem ai = (AuthorItem)book_author.getSelectedItem();
                //                parameters.add(ai.getId());
                //            }
            //            if(!isPublisher_All) {
                //                conditions.add("p.Publisher_ID = ? ");
                //                PublisherItem pi = (PublisherItem)book_publisher.getSelectedItem();
                //                parameters.add(pi.getId());
                //            }
            //            for (String condition : conditions) {
                //                baseQuery += " AND " + condition;
                //            }
            //
            //            try {
                //                    PreparedStatement ps = con.prepareStatement(baseQuery);
                //                    for (int i = 0; i < parameters.size(); i++) {
                    //                    ps.setObject(i+1, parameters.get(i));
                    //                }
                //                    ResultSet r = ps.executeQuery();
                //
                //                    ResultSetMetaData rsd = r.getMetaData();
                //                    int columns = rsd.getColumnCount();
                //
                //                    DefaultTableModel model = (DefaultTableModel)book_table.getModel();
                //                    model.setRowCount(0);
                //
                //                    while(r.next()){
                    //                        Object[] obj = new Object[columns];
                    //                        obj[0] = r.getString("Book_ID");
                    //                        obj[1] = r.getString("Name");
                    //                        obj[2] = new CategoryItem(r.getInt("Category_ID"), r.getString("Category"));
                    //                        obj[3] = new AuthorItem(r.getInt("Author_ID"), r.getString("Author"));
                    //                        obj[4] = new PublisherItem(r.getInt("Publisher_ID"), r.getString("Publisher"));
                    //                        obj[5] = r.getString("Quantity");
                    //                        model.addRow(obj);
                    //                    }
                //
                //                } catch (SQLException ex) {
                //                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
                //            }
            //        }
    }//GEN-LAST:event_book_publisherItemStateChanged

    private void book_categoryItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_book_categoryItemStateChanged
        // TODO add your handling code here:
        //        if(evt.getStateChange() == ItemEvent.SELECTED){
            //            if(!book_combobox_check.book_id_check || !book_combobox_check.book_category_check
                //                || !book_combobox_check.book_author_check || !book_combobox_check.book_publisher_check){
                //                return;
                //            }
            //                String baseQuery = "SELECT b.Book_ID, b.Name,c.Category_ID ,c.Name as Category,a.Author_ID,a.Name as Author,p.Publisher_ID,p.Name as Publisher, b.Quantity "
            //                    + "FROM `books`b JOIN categories c ON b.Category_ID = c.Category_ID "
            //                    + "JOIN authors a ON b.Author_ID = a.Author_ID JOIN publishers p ON b.Publisher_ID = p.Publisher_ID WHERE 1=1";
            //            ArrayList<String> conditions = new ArrayList<>();
            //            ArrayList<Object> parameters = new ArrayList<>();
            //            boolean isID_All = true;
            //            boolean isCategory_All = true;
            //            boolean isAuthor_All = true;
            //            boolean isPublisher_All = true;
            //
            //            if(book_id.getSelectedIndex() != 0) isID_All = false;
            //            if(book_category.getSelectedIndex() != 0) isCategory_All = false;
            //            if(book_author.getSelectedIndex() != 0) isAuthor_All = false;
            //            if(book_publisher.getSelectedIndex() != 0) isPublisher_All = false;
            //
            //            if(!isID_All) {
                //                conditions.add("b.Book_ID = ? ");
                //                parameters.add(book_id.getSelectedItem().toString());
                //            }
            //            if(!isCategory_All) {
                //                conditions.add("c.Category_ID = ? ");
                //                CategoryItem ci = (CategoryItem)book_category.getSelectedItem();
                //                parameters.add(ci.getId());
                //            }
            //            if(!isAuthor_All) {
                //                conditions.add("a.Author_ID = ? ");
                //                AuthorItem ai = (AuthorItem)book_author.getSelectedItem();
                //                parameters.add(ai.getId());
                //            }
            //            if(!isPublisher_All) {
                //                conditions.add("p.Publisher_ID = ? ");
                //                PublisherItem pi = (PublisherItem)book_publisher.getSelectedItem();
                //                parameters.add(pi.getId());
                //            }
            //            for (String condition : conditions) {
                //                baseQuery += " AND " + condition;
                //            }
            //
            //            try {
                //                    PreparedStatement ps = con.prepareStatement(baseQuery);
                //                    for (int i = 0; i < parameters.size(); i++) {
                    //                    ps.setObject(i+1, parameters.get(i));
                    //                }
                //                    ResultSet r = ps.executeQuery();
                //
                //                    ResultSetMetaData rsd = r.getMetaData();
                //                    int columns = rsd.getColumnCount();
                //
                //                    DefaultTableModel model = (DefaultTableModel)book_table.getModel();
                //                    model.setRowCount(0);
                //
                //                    while(r.next()){
                    //                        Object[] obj = new Object[columns];
                    //                        obj[0] = r.getString("Book_ID");
                    //                        obj[1] = r.getString("Name");
                    //                        obj[2] = new CategoryItem(r.getInt("Category_ID"), r.getString("Category"));
                    //                        obj[3] = new AuthorItem(r.getInt("Author_ID"), r.getString("Author"));
                    //                        obj[4] = new PublisherItem(r.getInt("Publisher_ID"), r.getString("Publisher"));
                    //                        obj[5] = r.getString("Quantity");
                    //                        model.addRow(obj);
                    //                    }
                //
                //                } catch (SQLException ex) {
                //                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
                //            }
            //        }
    }//GEN-LAST:event_book_categoryItemStateChanged

    private void book_searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_book_searchButtonActionPerformed
        // TODO add your handling code here:
        String baseQuery = "SELECT b.Book_ID, b.Name,c.Category_ID ,c.Name as Category,a.Author_ID,a.Name as Author,p.Publisher_ID,p.Name as Publisher, b.Quantity "
        + "FROM `books`b JOIN categories c ON b.Category_ID = c.Category_ID "
        + "JOIN authors a ON b.Author_ID = a.Author_ID JOIN publishers p ON b.Publisher_ID = p.Publisher_ID WHERE 1=1";
        ArrayList<String> conditions = new ArrayList<>();
        ArrayList<Object> parameters = new ArrayList<>();
        boolean isID_All = true;
        boolean isCategory_All = true;
        boolean isAuthor_All = true;
        boolean isPublisher_All = true;

        if(book_id_search.getSelectedIndex() != 0) isID_All = false;
        if(book_category_search.getSelectedIndex() != 0) isCategory_All = false;
        if(book_author_search.getSelectedIndex() != 0) isAuthor_All = false;
        if(book_publisher_search.getSelectedIndex() != 0) isPublisher_All = false;

        if(!isID_All) {
            conditions.add("b.Book_ID = ? ");
            parameters.add(book_id_search.getSelectedItem().toString());
        }
        if(!isCategory_All) {
            conditions.add("c.Category_ID = ? ");
            CategoryItem ci = (CategoryItem)book_category_search.getSelectedItem();
            parameters.add(ci.getId());
        }
        if(!isAuthor_All) {
            conditions.add("a.Author_ID = ? ");
            AuthorItem ai = (AuthorItem)book_author_search.getSelectedItem();
            parameters.add(ai.getId());
        }
        if(!isPublisher_All) {
            conditions.add("p.Publisher_ID = ? ");
            PublisherItem pi = (PublisherItem)book_publisher_search.getSelectedItem();
            parameters.add(pi.getId());
        }
        for (String condition : conditions) {
            baseQuery += " AND " + condition;
        }

        try {
            PreparedStatement ps = con.prepareStatement(baseQuery);
            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i+1, parameters.get(i));
            }
            ResultSet r = ps.executeQuery();

            ResultSetMetaData rsd = r.getMetaData();
            int columns = rsd.getColumnCount();

            DefaultTableModel model = (DefaultTableModel)book_table.getModel();
            model.setRowCount(0);

            while(r.next()){
                Object[] obj = new Object[columns];
                obj[0] = r.getString("Book_ID");
                obj[1] = r.getString("Name");
                obj[2] = new CategoryItem(r.getInt("Category_ID"), r.getString("Category"));
                obj[3] = new AuthorItem(r.getInt("Author_ID"), r.getString("Author"));
                obj[4] = new PublisherItem(r.getInt("Publisher_ID"), r.getString("Publisher"));
                obj[5] = r.getString("Quantity");
                model.addRow(obj);
            }

        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_book_searchButtonActionPerformed

    private void book_borrowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_book_borrowButtonActionPerformed
        // TODO add your handling code here:
        String IDCurrentBook = ((DefaultTableModel)book_table.getModel()).getValueAt(book_table.getSelectedRow(),0).toString();
        String sql_cmd_1 = "SELECT issuebooks.Status FROM issuebooks WHERE issuebooks.UserName = ? AND issuebooks.Book_ID = ? ";
        String sql_cmd_UpdateCountBook = "UPDATE books SET books.Quantity = books.Quantity - 1 WHERE books.Book_ID = ? ";
        
        try{
            pst = con.prepareStatement(sql_cmd_1);
            pst.setString(1, AccountName);
            pst.setString(2, IDCurrentBook);
            rs = pst.executeQuery();
            if(!rs.next()){//Trường hợp User chưa hề có hành động mượn trả sách này
                con.setAutoCommit(false);
                PreparedStatement pst_UpdateCountBook = con.prepareStatement(sql_cmd_UpdateCountBook);
                pst_UpdateCountBook.setString(1, IDCurrentBook);
                pst_UpdateCountBook.executeUpdate();
                
                String sql_cmd_InsertNewIssue = "INSERT INTO issuebooks (Book_ID, UserName, IssueDate, DueDate, Status) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pst_UpdateInsertIssues = con.prepareStatement(sql_cmd_InsertNewIssue);
                pst_UpdateInsertIssues.setString(1, book_id.getText());
                pst_UpdateInsertIssues.setString(2, AccountName);
                pst_UpdateInsertIssues.setString(3, LocalDateTime.now().format(formatter));
                pst_UpdateInsertIssues.setString(4, LocalDateTime.now().plusDays(14).format(formatter));
                pst_UpdateInsertIssues.setString(5, "Borrowed");
                pst_UpdateInsertIssues.executeUpdate();
                
                con.commit();
                
                con.setAutoCommit(true);
            }
            else{// Đã từng có lịch sử mượn trả sách 
                if(rs.getString(1).equals("Borrowed") == true || rs.getString(1).equals("OverTime") == true){// Đang mượn hoặc đang quá hạn(Cũng là mượn).
                    JOptionPane.showMessageDialog(this, "This book is borrowing");
                }
                else{//Đã từng mượn và đã trả
                    con.setAutoCommit(false);
                
                    PreparedStatement pst_UpdateCountBook = con.prepareStatement(sql_cmd_UpdateCountBook);
                    pst_UpdateCountBook.setString(1, IDCurrentBook);
                    pst_UpdateCountBook.executeUpdate();

                    String sql_cmd_UpdateStatus = "UPDATE issuebooks SET issuebooks.Status = ? , issuebooks.IssueDate = ? , issuebooks.DueDate = ? WHERE issuebooks.UserName = ? AND issuebooks.Book_ID = ? ";
                    PreparedStatement pst_UpdateStatus = con.prepareStatement(sql_cmd_UpdateStatus);
                    pst_UpdateStatus.setString(1, "Borrowed");
                    pst_UpdateStatus.setString(2, LocalDateTime.now().format(formatter));
                    pst_UpdateStatus.setString(3, LocalDateTime.now().plusDays(14).format(formatter));
                    pst_UpdateStatus.setString(4, AccountName);
                    pst_UpdateStatus.setString(5, IDCurrentBook);
                    pst_UpdateStatus.executeUpdate();

                    con.commit();

                    con.setAutoCommit(true);
                }
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
        Book_panelLoad();
    }//GEN-LAST:event_book_borrowButtonActionPerformed

    private void book_reviewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_book_reviewButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_book_reviewButtonActionPerformed

    private void view_book_search_jscrollMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_view_book_search_jscrollMouseClicked
        // TODO add your handling code here:
        SearchBook_panel_Load();
    }//GEN-LAST:event_view_book_search_jscrollMouseClicked

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
            @Override
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
    private app.bolivia.swing.JCTextField bookName_text;
    private javax.swing.JComboBox<AuthorItem> book_author;
    private javax.swing.JComboBox book_author_search;
    private javax.swing.JButton book_borrowButton;
    private javax.swing.JComboBox<CategoryItem> book_category;
    private javax.swing.JComboBox book_category_search;
    private javax.swing.JTextField book_id;
    private javax.swing.JLabel book_id_label;
    private javax.swing.JComboBox book_id_search;
    private javax.swing.JTextField book_name;
    private javax.swing.JLabel book_name_label;
    private javax.swing.JPanel book_panel;
    private javax.swing.JComboBox<PublisherItem> book_publisher;
    private javax.swing.JComboBox book_publisher_search;
    private javax.swing.JTextField book_quantity;
    private javax.swing.JButton book_reviewButton;
    private javax.swing.JButton book_searchButton;
    private rojeru_san.complementos.RSTableMetro book_table;
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
    private javax.swing.JButton goToLibrary_button;
    private javax.swing.JButton home_page_button;
    private javax.swing.JPanel home_page_panel;
    private javax.swing.JLabel icon_member;
    private app.bolivia.swing.JCTextField id_text;
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
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
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
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
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
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JButton logout_button;
    private javax.swing.JLabel member_infor_label;
    private javax.swing.JButton myBookShelf_button;
    private com.github.lgooddatepicker.components.DatePicker ngayMuon_date;
    private javax.swing.JLabel ngayMuon_label;
    private com.github.lgooddatepicker.components.DatePicker ngayTra_date;
    private javax.swing.JLabel ngayTra_label;
    private javax.swing.JLabel overtimeNumber;
    private javax.swing.JLabel returnedNumber;
    private javax.swing.JPanel search_panel;
    private javax.swing.JPanel searchbook_panel;
    private javax.swing.JButton timKiem_button;
    private javax.swing.JLabel tim_kiem_panel;
    private javax.swing.JButton toanBo_button;
    private javax.swing.JButton traSach_button;
    private javax.swing.JComboBox<String> trangThai_cbbox;
    private javax.swing.JLabel trang_thai_panel;
    private app.bolivia.swing.JCTextField txtpassword;
    private app.bolivia.swing.JCTextField txtpassword1;
    private app.bolivia.swing.JCTextField txtpassword2;
    private javax.swing.JLabel user_name;
    private rojeru_san.complementos.RSTableMetro view_book_jscroll;
    private rojeru_san.complementos.RSTableMetro view_book_search_jscroll;
    private javax.swing.JScrollPane view_search_book_jscroll;
    // End of variables declaration//GEN-END:variables
}
