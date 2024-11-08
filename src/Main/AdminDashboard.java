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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Admin
 */
public class AdminDashboard extends javax.swing.JFrame {
    private String AccountName;
    class Table_status {
        public int Previous_category_selected;
        public int Previous_author_selected;

        public Table_status() {
            this.Previous_category_selected = -1;
            this.Previous_author_selected = -1;
        }
        
    }
    Table_status table_status = new Table_status();
    /**
     * Creates new form AdminDashboard
     */
    public AdminDashboard(String AccountName) {
        initComponents();
        this.AccountName = AccountName;
        Connect();
        AdminGreeting_Load();
    }
    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    public void Connect(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
                    con = DriverManager.getConnection("jdbc:mysql://localhost/" + Database.DB_Name,Database.DB_UserName,Database.DB_Password);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
//Load AdminGreeting in 
    private void AdminGreeting_Load() {
//        Recent_Issue_Load();
        admin_name.setText("Welcome, Admin " + AccountName);
        try {
            pst = con.prepareStatement("SELECT count(*) FROM Books UNION ALL SELECT count(*) FROM Accounts WHERE Role = \"User\" UNION ALL SELECT count(*) FROM Issuebooks");
            rs = pst.executeQuery();
            rs.next();
            BookNumber.setText("    " + rs.getString(1));
            rs.next();
            UserNumber.setText("    " + rs.getString(1));
            rs.next();
            IssueNumber.setText("    " + rs.getString(1));
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
//---------------------------------------------------------------------------------------------------------------------------------------------
//Initialize Home_page    
    private void Home_page_Load() {
        Recent_Issue_Load();
    }
//Load recent_issue_table in Home_page
    private void Recent_Issue_Load() {
        String sql_cmd = "SELECT member.ID, member.MemberName, book.BookName, ib.CheckoutDate "
                + "FROM (SELECT * FROM issuebook ORDER BY ID DESC LIMIT 5) AS ib "
                + "JOIN member ON ib.MemberID = member.ID JOIN book ON ib.BookID = book.ID;";
        try {
            pst = con.prepareStatement(sql_cmd);
            rs = pst.executeQuery();            
            DefaultTableModel model = (DefaultTableModel)recent_issue.getModel();
            model.setNumRows(0);
            while(rs.next()) {
                Object[] obj = {rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4)};
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
        manage_categories_button.setBackground(color);
        manage_authors_button.setBackground(color);
        logout_button.setBackground(color);
    }
//---------------------------------------------------------------------------------------------------------------------------------------------
//Initialize category_panel
    private void Category_panelLoad() {
        Category_TableLoad();
        Category_IDLoad();
        Category_buttonLoad();
        Category_TextandComboboxLoad();
    }   
//Load button_status when initialize category_panel
    private void Category_buttonLoad() {
        category_addButton.setEnabled(true);
        category_updateButton.setEnabled(false);
        category_deleteButton.setEnabled(false);
    }
//Load text and combobox status when initialize category_panel
    private void Category_TextandComboboxLoad() {
        category_id.setSelectedIndex(-1);
        category_name.setText("");
        category_status.setSelectedIndex(-1);
        category_book_count.setText("");
    }
// Load category_table in category_panel
    private void Category_TableLoad(){
            try {
                pst = con.prepareStatement("select * from categories");
                rs = pst.executeQuery();

                ResultSetMetaData rsd = rs.getMetaData();
                int columns = rsd.getColumnCount();

                DefaultTableModel model = (DefaultTableModel)category_table.getModel();
                model.setRowCount(0);

                while(rs.next()){
                    Object[] obj = new Object[columns];
                    for(int i = 1 ; i <= columns ; i++){
                        obj[i-1] = rs.getString(i);
                    }
                    model.addRow(obj);
                }

            } catch (SQLException ex) {
                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }       
        }
//Load category id into category_id combobox
    private void Category_IDLoad(){
        try {
            pst = con.prepareStatement("Select * from Categories");
            rs = pst.executeQuery();
            category_id.removeAllItems();
             
            while(rs.next()){
                category_id.addItem(rs.getString("Category_ID"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }           
    } 
//---------------------------------------------------------------------------------------------------------------------------------------------
//Initialize category_panel
    private void Author_panelLoad() {
        Author_TableLoad();
        Author_IDLoad();
        Author_buttonLoad();
        Author_TextandComboboxLoad();
    }   
//Load button_status when initialize category_panel
    private void Author_buttonLoad() {
        author_addButton.setEnabled(true);
        author_updateButton.setEnabled(false);
        author_deleteButton.setEnabled(false);
    }
//Load text and combobox status when initialize category_panel
    private void Author_TextandComboboxLoad() {
        author_id.setSelectedIndex(-1);
        author_name.setText("");
        author_city.setText("");
        author_book_count.setText("");
    }
// Load category_table in category_panel
    private void Author_TableLoad(){
            try {
                pst = con.prepareStatement("select * from Authors");
                rs = pst.executeQuery();

                ResultSetMetaData rsd = rs.getMetaData();
                int columns = rsd.getColumnCount();

                DefaultTableModel model = (DefaultTableModel)author_table.getModel();
                model.setRowCount(0);

                while(rs.next()){
                    Object[] obj = new Object[columns];
                    for(int i = 1 ; i <= columns ; i++){
                        obj[i-1] = rs.getString(i);
                    }
                    model.addRow(obj);
                }

            } catch (SQLException ex) {
                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }       
        }
//Load category id into category_id combobox
    private void Author_IDLoad(){
        try {
            pst = con.prepareStatement("Select * from Authors");
            rs = pst.executeQuery();
            author_id.removeAllItems();
             
            while(rs.next()){
                author_id.addItem(rs.getString("Author_ID"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        admin_name = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        Logout_panel = new javax.swing.JPanel();
        manage_categories_button = new javax.swing.JButton();
        home_page_button = new javax.swing.JButton();
        logout_button = new javax.swing.JButton();
        manage_authors_button = new javax.swing.JButton();
        Parent_panel = new javax.swing.JPanel();
        home_page_panel = new javax.swing.JPanel();
        JPanel = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        UserNumber = new javax.swing.JLabel();
        JLabel = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        BookNumber = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        IssueNumber = new javax.swing.JLabel();
        j = new javax.swing.JLabel();
        Jlabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        recent_issue = new rojeru_san.complementos.RSTableMetro();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        rSTableMetro2 = new rojeru_san.complementos.RSTableMetro();
        jLabel21 = new javax.swing.JLabel();
        category_panel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        category_table = new rojeru_san.complementos.RSTableMetro();
        jPanel6 = new javax.swing.JPanel();
        category_id = new javax.swing.JComboBox<>();
        category_name = new javax.swing.JTextField();
        category_status = new javax.swing.JComboBox<>();
        category_book_count = new javax.swing.JTextField();
        category_addButton = new javax.swing.JButton();
        category_updateButton = new javax.swing.JButton();
        category_deleteButton = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        author_panel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        author_table = new rojeru_san.complementos.RSTableMetro();
        jPanel7 = new javax.swing.JPanel();
        author_id = new javax.swing.JComboBox<>();
        author_book_count = new javax.swing.JTextField();
        author_addButton = new javax.swing.JButton();
        author_updateButton = new javax.swing.JButton();
        author_deleteButton = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        author_name = new javax.swing.JTextField();
        author_city = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jPanel2.setBackground(new java.awt.Color(102, 102, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_menu_48px_1.png"))); // NOI18N
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 50, 50));

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 0, 3, 50));

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI", 1, 20)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Library Management System");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, 310, 30));

        admin_name.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        admin_name.setForeground(new java.awt.Color(255, 255, 255));
        admin_name.setText("Welcome, Admin ");
        jPanel2.add(admin_name, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 10, 230, 30));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/male_user_50px.png"))); // NOI18N
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 0, -1, -1));

        jLabel6.setFont(new java.awt.Font("Tw Cen MT", 1, 36)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("X");
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
        });
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(1120, 10, 30, 30));

        jPanel3.setBackground(new java.awt.Color(51, 51, 51));
        jPanel3.setForeground(new java.awt.Color(51, 51, 51));

        jPanel5.setBackground(new java.awt.Color(51, 51, 51));

        jLabel7.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Feature");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 7, Short.MAX_VALUE)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        Logout_panel.setBackground(new java.awt.Color(51, 51, 51));

        javax.swing.GroupLayout Logout_panelLayout = new javax.swing.GroupLayout(Logout_panel);
        Logout_panel.setLayout(Logout_panelLayout);
        Logout_panelLayout.setHorizontalGroup(
            Logout_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 237, Short.MAX_VALUE)
        );
        Logout_panelLayout.setVerticalGroup(
            Logout_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 39, Short.MAX_VALUE)
        );

        manage_categories_button.setBackground(new java.awt.Color(51, 51, 51));
        manage_categories_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        manage_categories_button.setForeground(new java.awt.Color(255, 255, 255));
        manage_categories_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Sell_26px.png"))); // NOI18N
        manage_categories_button.setText("Manage Categories");
        manage_categories_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        manage_categories_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manage_categories_buttonActionPerformed(evt);
            }
        });

        home_page_button.setBackground(new java.awt.Color(255, 0, 51));
        home_page_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        home_page_button.setForeground(new java.awt.Color(255, 255, 255));
        home_page_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/home_24px.png"))); // NOI18N
        home_page_button.setText("Home Page");
        home_page_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        home_page_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                home_page_buttonActionPerformed(evt);
            }
        });

        logout_button.setBackground(new java.awt.Color(51, 51, 51));
        logout_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        logout_button.setForeground(new java.awt.Color(255, 255, 255));
        logout_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Exit_26px.png"))); // NOI18N
        logout_button.setText("Logout");
        logout_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        logout_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logout_buttonActionPerformed(evt);
            }
        });

        manage_authors_button.setBackground(new java.awt.Color(51, 51, 51));
        manage_authors_button.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        manage_authors_button.setForeground(new java.awt.Color(255, 255, 255));
        manage_authors_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Exit_26px.png"))); // NOI18N
        manage_authors_button.setText("Manage Authors");
        manage_authors_button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        manage_authors_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manage_authors_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Logout_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(manage_categories_button, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
            .addComponent(home_page_button, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
            .addComponent(logout_button, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
            .addComponent(manage_authors_button, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(home_page_button, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(manage_categories_button, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(manage_authors_button, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(logout_button, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 184, Short.MAX_VALUE)
                .addComponent(Logout_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(116, 116, 116))
        );

        Parent_panel.setLayout(new java.awt.CardLayout());

        home_page_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel8.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(255, 0, 51)));

        UserNumber.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        UserNumber.setForeground(new java.awt.Color(102, 102, 102));
        UserNumber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_People_50px.png"))); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(UserNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(UserNumber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        JLabel.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        JLabel.setForeground(new java.awt.Color(102, 102, 102));
        JLabel.setText("No Of Books");

        jPanel9.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(255, 0, 51)));

        BookNumber.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        BookNumber.setForeground(new java.awt.Color(102, 102, 102));
        BookNumber.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BookNumber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Book_Shelf_50px.png"))); // NOI18N

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BookNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(43, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(BookNumber)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createMatteBorder(15, 0, 0, 0, new java.awt.Color(255, 0, 51)));

        IssueNumber.setFont(new java.awt.Font("Segoe UI", 0, 28)); // NOI18N
        IssueNumber.setForeground(new java.awt.Color(102, 102, 102));
        IssueNumber.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Sell_50px.png"))); // NOI18N

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(IssueNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(43, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(IssueNumber)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        j.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        j.setForeground(new java.awt.Color(102, 102, 102));
        j.setText("Issue Books");

        Jlabel.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        Jlabel.setForeground(new java.awt.Color(102, 102, 102));
        Jlabel.setText("No Of Users");

        recent_issue.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "UserID", "UserName", "Book", "CheckoutDate"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        recent_issue.setColorFilasBackgound2(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(recent_issue);

        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jLabel20.setText("Most-issued Books");

        rSTableMetro2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "BookID", "BookName", "Category", "No of issues"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        rSTableMetro2.setColorFilasBackgound2(new java.awt.Color(255, 255, 255));
        jScrollPane2.setViewportView(rSTableMetro2);

        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jLabel21.setText("Recent Issues");

        javax.swing.GroupLayout JPanelLayout = new javax.swing.GroupLayout(JPanel);
        JPanel.setLayout(JPanelLayout);
        JPanelLayout.setHorizontalGroup(
            JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(JLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Jlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(88, 88, 88)
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(j, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(JPanelLayout.createSequentialGroup()
                .addGap(121, 121, 121)
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 585, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 585, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        JPanelLayout.setVerticalGroup(
            JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(JLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(j, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Jlabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(JPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        home_page_panel.add(JPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 920, -1));

        Parent_panel.add(home_page_panel, "card2");

        category_panel.setBackground(new java.awt.Color(255, 255, 255));
        category_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        category_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Category_ID", "Name", "Status", "Book_count"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        category_table.setColorFilasBackgound2(new java.awt.Color(153, 255, 153));
        category_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                category_tableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(category_table);

        category_panel.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 180, 580, 470));

        jPanel6.setBackground(new java.awt.Color(255, 51, 102));

        category_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                category_idActionPerformed(evt);
            }
        });

        category_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Active", "Inactive" }));

        category_addButton.setText("ADD");
        category_addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                category_addButtonActionPerformed(evt);
            }
        });

        category_updateButton.setText("UPDATE");
        category_updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                category_updateButtonActionPerformed(evt);
            }
        });

        category_deleteButton.setText("DELETE");
        category_deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                category_deleteButtonActionPerformed(evt);
            }
        });

        jLabel15.setText("Category_ID");

        jLabel16.setText("Name");

        jLabel17.setText("Status");

        jLabel18.setText("Book_count");

        jLabel19.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Info");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(category_addButton)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(category_updateButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(category_deleteButton)
                                .addGap(9, 9, 9))
                            .addComponent(category_name)
                            .addComponent(category_id, 0, 185, Short.MAX_VALUE)
                            .addComponent(category_status, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(category_book_count)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(96, 96, 96)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(87, 87, 87)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(80, 80, 80)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(category_id))
                .addGap(33, 33, 33)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(category_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(34, 34, 34)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(category_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addGap(26, 26, 26)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(category_book_count, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addGap(58, 58, 58)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(category_addButton)
                    .addComponent(category_updateButton)
                    .addComponent(category_deleteButton))
                .addContainerGap(177, Short.MAX_VALUE))
        );

        category_panel.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 340, 650));

        jLabel14.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/adminIcons/icons8_Book_Shelf_50px.png"))); // NOI18N
        jLabel14.setText("Category");
        category_panel.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 60, 220, 50));

        Parent_panel.add(category_panel, "card3");

        author_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Author_ID", "Name", "City", "Book_count"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        author_table.setColorFilasBackgound2(new java.awt.Color(204, 255, 102));
        author_table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        author_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                author_tableMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(author_table);

        jPanel7.setBackground(new java.awt.Color(255, 51, 102));

        author_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                author_idActionPerformed(evt);
            }
        });

        author_addButton.setText("ADD");
        author_addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                author_addButtonActionPerformed(evt);
            }
        });

        author_updateButton.setText("UPDATE");
        author_updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                author_updateButtonActionPerformed(evt);
            }
        });

        author_deleteButton.setText("DELETE");
        author_deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                author_deleteButtonActionPerformed(evt);
            }
        });

        jLabel22.setText("Author_ID");

        jLabel23.setText("Name");

        jLabel24.setText("City");

        jLabel25.setText("Book_count");

        jLabel26.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("Info");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(author_addButton)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(author_updateButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(author_deleteButton)
                                .addGap(9, 9, 9))
                            .addComponent(author_id, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(author_book_count)
                            .addComponent(author_name)
                            .addComponent(author_city, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(96, 96, 96)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(87, 87, 87)
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(80, 80, 80)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(author_id))
                .addGap(33, 33, 33)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(author_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(author_city, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(author_book_count, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25))
                .addGap(58, 58, 58)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(author_addButton)
                    .addComponent(author_updateButton)
                    .addComponent(author_deleteButton))
                .addContainerGap(177, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout author_panelLayout = new javax.swing.GroupLayout(author_panel);
        author_panel.setLayout(author_panelLayout);
        author_panelLayout.setHorizontalGroup(
            author_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, author_panelLayout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 576, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        author_panelLayout.setVerticalGroup(
            author_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(author_panelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(author_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        Parent_panel.add(author_panel, "card4");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(Parent_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 915, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Parent_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jLabel6MouseClicked

    private void category_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_category_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_category_idActionPerformed

    private void category_addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_category_addButtonActionPerformed
        // TODO add your handling code here:
        String name = category_name.getText();
        String status = category_status.getSelectedItem().toString();
        String book_count = "0";
        try {
            pst = con.prepareStatement("Insert into Categories(Name,Status,Book_count)values(?,?,?)");
            pst.setString(1, name);
            pst.setString(2, status);
            pst.setString(3, book_count);
            int k = pst.executeUpdate();            
            if(k == 1){
                JOptionPane.showMessageDialog(this, "Category created");
                category_id.setSelectedIndex(-1);
                category_name.setText("");
                category_status.setSelectedIndex(-1);
                category_book_count.setText("");
                Category_TableLoad();
                Category_IDLoad();
            } else {
                JOptionPane.showMessageDialog(this, "Error");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_category_addButtonActionPerformed

    private void category_updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_category_updateButtonActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel)category_table.getModel();
        int selectIndex = category_table.getSelectedRow();
        String id = model.getValueAt(selectIndex, 0).toString();        
        String name = category_name.getText();
        String status = category_status.getSelectedItem().toString();
        String book_count = category_book_count.getText();
        try {
            pst = con.prepareStatement("Update Categories set Name = ?, Status = ?, Book_count = ? where Category_ID = ?");
            pst.setString(1, name);
            pst.setString(2, status);
            pst.setString(3, book_count);
            pst.setString(4, id);
            int k = pst.executeUpdate();            
            if(k == 1){
                JOptionPane.showMessageDialog(this, "Category updated");
                category_name.setText("");
                category_status.setSelectedIndex(-1);
                category_id.setSelectedIndex(-1);
                category_book_count.setText("");
                Category_TableLoad();
                category_addButton.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Error");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_category_updateButtonActionPerformed

    private void category_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_category_tableMouseClicked
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel)category_table.getModel();
        int clickedRow = category_table.rowAtPoint(evt.getPoint());
        if(clickedRow == -1) return;
        if(clickedRow == table_status.Previous_category_selected) {
            table_status.Previous_category_selected = -1;
            category_table.clearSelection();
            category_addButton.setEnabled(true);
            category_updateButton.setEnabled(false);
            category_deleteButton.setEnabled(false);
            return;
        }
        table_status.Previous_category_selected = clickedRow;
        int selectIndex = category_table.getSelectedRow();
        category_id.setSelectedItem(model.getValueAt(selectIndex,0).toString());
        category_name.setText(model.getValueAt(selectIndex,1).toString());
        category_status.setSelectedItem(model.getValueAt(selectIndex,2).toString());   
        category_book_count.setText(model.getValueAt(selectIndex,3).toString());
        category_addButton.setEnabled(false);
        category_updateButton.setEnabled(true);
        category_deleteButton.setEnabled(true);
    }//GEN-LAST:event_category_tableMouseClicked

    private void category_deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_category_deleteButtonActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel)category_table.getModel();
        int selectIndex = category_table.getSelectedRow();
        String id = model.getValueAt(selectIndex, 0).toString();
        try {
            pst = con.prepareStatement("Delete from Categories Where Category_ID = ?");
            pst.setString(1, id);
            int k = pst.executeUpdate();
            
            if(k == 1){
                JOptionPane.showMessageDialog(this, "Data deleted");
                category_name.setText("");
                category_id.setSelectedIndex(-1);
                category_book_count.setText("");
                category_status.setSelectedIndex(-1);
                Category_TableLoad();
                Category_IDLoad();
                category_addButton.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Error");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }//GEN-LAST:event_category_deleteButtonActionPerformed

    private void manage_categories_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manage_categories_buttonActionPerformed
        // TODO add your handling code here:
        Parent_panel.removeAll();
        Parent_panel.add(category_panel);
        Parent_panel.repaint();
        Parent_panel.revalidate();
        Category_panelLoad();
        TurnOffButtons();
        manage_categories_button.setBackground(new Color(255,0,51));
    }//GEN-LAST:event_manage_categories_buttonActionPerformed

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

    private void manage_authors_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manage_authors_buttonActionPerformed
        // TODO add your handling code here:
        Parent_panel.removeAll();
        Parent_panel.add(author_panel);
        Parent_panel.repaint();
        Parent_panel.revalidate();
        Author_panelLoad();
        TurnOffButtons();
        manage_authors_button.setBackground(new Color(255,0,51));
    }//GEN-LAST:event_manage_authors_buttonActionPerformed

    private void author_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_author_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_author_idActionPerformed

    private void author_addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_author_addButtonActionPerformed
        // TODO add your handling code here:
        String name = author_name.getText();
        String city = author_city.getText();
        String book_count = "0";
        try {
            pst = con.prepareStatement("Insert into Authors(Name,City,Book_count)values(?,?,?)");
            pst.setString(1, name);
            pst.setString(2, city);
            pst.setString(3, book_count);
            int k = pst.executeUpdate();            
            if(k == 1){
                JOptionPane.showMessageDialog(this, "Author created");
                author_id.setSelectedIndex(-1);
                author_name.setText("");
                author_city.setText("");
                author_book_count.setText("");
                Author_TableLoad();
                Author_IDLoad();
            } else {
                JOptionPane.showMessageDialog(this, "Error");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_author_addButtonActionPerformed

    private void author_updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_author_updateButtonActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel)author_table.getModel();
        int selectIndex = author_table.getSelectedRow();
        String id = model.getValueAt(selectIndex, 0).toString();        
        String name = admin_name.getText();
        String city = author_city.getText();
        String book_count = author_book_count.getText();
        try {
            pst = con.prepareStatement("Update Authors set Name = ?, City = ?, Book_count = ? where Author_ID = ?");
            pst.setString(1, name);
            pst.setString(2, city);
            pst.setString(3, book_count);
            pst.setString(4, id);
            int k = pst.executeUpdate();            
            if(k == 1){
                JOptionPane.showMessageDialog(this, "Data updated");
                author_name.setText("");
                author_city.setText("");
                author_id.setSelectedIndex(-1);
                author_book_count.setText("");
                Author_TableLoad();
                author_addButton.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Error");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_author_updateButtonActionPerformed

    private void author_deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_author_deleteButtonActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel)author_table.getModel();
        int selectIndex = author_table.getSelectedRow();
        String id = model.getValueAt(selectIndex, 0).toString();
        try {
            pst = con.prepareStatement("Delete from Authors Where Author_ID = ?");
            pst.setString(1, id);
            int k = pst.executeUpdate();
            
            if(k == 1){
                JOptionPane.showMessageDialog(this, "Data deleted");
                author_name.setText("");
                author_id.setSelectedIndex(-1);
                author_book_count.setText("");
                author_city.setText("");
                Author_TableLoad();
                Author_IDLoad();
                author_addButton.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Error");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }//GEN-LAST:event_author_deleteButtonActionPerformed

    private void author_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_author_tableMouseClicked
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel)author_table.getModel();
        int clickedRow = author_table.rowAtPoint(evt.getPoint());
        if(clickedRow == -1) return;
        if(clickedRow == table_status.Previous_author_selected) {
            table_status.Previous_author_selected = -1;
            author_table.clearSelection();
            author_addButton.setEnabled(true);
            author_updateButton.setEnabled(false);
            author_deleteButton.setEnabled(false);
            return;
        }
        table_status.Previous_author_selected = clickedRow;
        int selectIndex = author_table.getSelectedRow();
        author_id.setSelectedItem(model.getValueAt(selectIndex,0).toString());
        author_name.setText(model.getValueAt(selectIndex,1).toString());
        author_city.setText(model.getValueAt(selectIndex,2).toString());   
        author_book_count.setText(model.getValueAt(selectIndex,3).toString());
        author_addButton.setEnabled(false);
        author_updateButton.setEnabled(true);
        author_deleteButton.setEnabled(true);
    }//GEN-LAST:event_author_tableMouseClicked

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
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminDashboard("Tester").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BookNumber;
    private javax.swing.JLabel IssueNumber;
    private javax.swing.JLabel JLabel;
    private javax.swing.JPanel JPanel;
    private javax.swing.JLabel Jlabel;
    private javax.swing.JPanel Logout_panel;
    private javax.swing.JPanel Parent_panel;
    private javax.swing.JLabel UserNumber;
    private javax.swing.JLabel admin_name;
    private javax.swing.JButton author_addButton;
    private javax.swing.JTextField author_book_count;
    private javax.swing.JTextField author_city;
    private javax.swing.JButton author_deleteButton;
    private javax.swing.JComboBox<String> author_id;
    private javax.swing.JTextField author_name;
    private javax.swing.JPanel author_panel;
    private rojeru_san.complementos.RSTableMetro author_table;
    private javax.swing.JButton author_updateButton;
    private javax.swing.JButton category_addButton;
    private javax.swing.JTextField category_book_count;
    private javax.swing.JButton category_deleteButton;
    private javax.swing.JComboBox<String> category_id;
    private javax.swing.JTextField category_name;
    private javax.swing.JPanel category_panel;
    private javax.swing.JComboBox<String> category_status;
    private rojeru_san.complementos.RSTableMetro category_table;
    private javax.swing.JButton category_updateButton;
    private javax.swing.JButton home_page_button;
    private javax.swing.JPanel home_page_panel;
    private javax.swing.JLabel j;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JButton logout_button;
    private javax.swing.JButton manage_authors_button;
    private javax.swing.JButton manage_categories_button;
    private rojeru_san.complementos.RSTableMetro rSTableMetro2;
    private rojeru_san.complementos.RSTableMetro recent_issue;
    // End of variables declaration//GEN-END:variables
}
