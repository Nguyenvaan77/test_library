/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Main;

import com.github.lgooddatepicker.components.DatePickerSettings;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Lenovo
 */
public class Library extends javax.swing.JFrame {
    private String AccountName;
    /**
     * Creates new form test
     */
    public Library(String AccountName) {
        this.AccountName = AccountName;
        initComponents();
        Connect();
        Library_greetingLoad();
        Library_Load();
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
    
//---------------------------------------------------------------------------------------------------
//  *** Search Page
//  0. Load greetingLibraryPage
    private void Library_greetingLoad(){
        Lib_id_text.setText("");
        Lib_bookName_text.setText("");
        Lib_cate_text.setText("");
        Lib_author_text.setText("");
        Lib_publisher_text.setText("");
        Lib_quantity_consttext.setText("0");
    }
//  1. Initialize search_page
    private void Library_Load() {
        Library_panel_Load();
        Library_view_bookJSC_Load();
    }

    //  1.2 Load buttons, text and comboBox of Search panel
    private void Library_panel_Load() {
        Library_textLoad();
        Library_buttonLoad();
    }
    //  1.3 Load jscrool which review book's information
    private void Library_view_bookJSC_Load() {
        String sql_cmd = " SELECT books.Book_ID, books.Name, books.Category_ID, books.Author_ID, books.Publisher_ID, books.Quantity "
                + " FROM books "
                + " WHERE books.Book_ID LIKE ? AND books.Name LIKE ? AND books.Category_ID LIKE ? AND books.Author_ID LIKE ? AND books.Publisher_ID LIKE ? ";
        try {
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1, "%" + Lib_id_text.getText() + "%");
            pst.setString(2, "%" + Lib_bookName_text.getText() + "%");
            pst.setString(3, "%" + Lib_cate_text.getText() + "%");
            pst.setString(4, "%" + Lib_author_text.getText() + "%");
            pst.setString(5, "%" + Lib_publisher_text.getText() + "%");
            rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel) Lib_view_book_ljscroll.getModel();
            model.setNumRows(0);
            while (rs.next()) {
                Object[] obj = {rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)};
                model.addRow(obj);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //  1.2.1 Load buttons of search panel
    private void Library_buttonLoad() {
        Lib_toanBo_button.setEnabled(true);
        Lib_timKiem_button.setEnabled(true);
        if(Lib_quantity_consttext.getText().equals("0") == true){
            Lib_muonSach_button.setEnabled(false);
        }
        else{
        Lib_muonSach_button.setEnabled(true);
        }
    }

    //  1.2.2 Load text od search panel
    private void Library_textLoad() {
        String sql_cmd = " SELECT books.Quantity "
                + " FROM books "
                + " WHERE books.Book_ID LIKE ? AND books.Name LIKE ? AND books.Category_ID LIKE ? AND books.Author_ID LIKE ? AND books.Publisher_ID LIKE ? ";
        try {
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1, "%" + Lib_id_text.getText() + "%");
            pst.setString(2, "%" + Lib_bookName_text.getText() + "%");
            pst.setString(3, "%" + Lib_cate_text.getText() + "%");
            pst.setString(4, "%" + Lib_author_text.getText() + "%");
            pst.setString(5, "%" + Lib_publisher_text.getText() + "%");
            rs = pst.executeQuery();
            rs.next();
            Lib_quantity_consttext.setText(rs.getString(1));
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //1.2.2.* Load and show information's book which is selected in jscroll
    private void LoadAndShowInfor(String bookName){
        String sql_cmd = " SELECT books.Book_ID, books.Name, books.Category_ID, books.Author_ID, books.Publisher_ID, books.Quantity "
                + " FROM books "
                + " WHERE books.Book_ID LIKE ? AND books.Name LIKE ? AND books.Category_ID LIKE ? AND books.Author_ID LIKE ? AND books.Publisher_ID LIKE ? ";
        try {
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1, "%" + Lib_id_text.getText() + "%");
            pst.setString(2, "%" + Lib_bookName_text.getText() + "%");
            pst.setString(3, "%" + Lib_cate_text.getText() + "%");
            pst.setString(4, "%" + Lib_author_text.getText() + "%");
            pst.setString(5, "%" + Lib_publisher_text.getText() + "%");
            rs = pst.executeQuery();
            rs.next();
            Lib_quantity_consttext.setText(rs.getString(1));
            
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//******************************************************************************
    //  *** Return Book 
//  1. Check What Row is chosen
    private int indexOfRow() {// trả về thứ tự của dòng trong bảng bắt đầu từ 0, nếu không có dòng nào được chọn thì trả về - 1
        return Lib_view_book_ljscroll.getSelectedRow();
    }
    //  2. Return Book
    private boolean returnBook() {
        int indexBook = indexOfRow();

        if (indexOfRow() == -1 || Lib_view_book_ljscroll.getValueAt(indexBook, 4).equals("Returned")) {
            return false;
        }

        String sql_cmd = "UPDATE issuebooks SET issuebooks.Status = ? WHERE issuebooks.Book_ID = ? ";
        try {
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1, "Returned");
            pst.setString(2, (String) Lib_view_book_ljscroll.getValueAt(indexBook, 0));
            if(pst.executeUpdate() > 0){
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
//*****************************************************************************
//--------------------------------------------------------------------------------------------------------------------------------------
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        home_page_panel = new javax.swing.JPanel();
        librarypage_panel = new javax.swing.JPanel();
        Lib_view_book_jscrollPane = new javax.swing.JScrollPane();
        Lib_view_book_ljscroll = new rojeru_san.complementos.RSTableMetro();
        library_panel = new javax.swing.JPanel();
        library_label = new javax.swing.JLabel();
        Lib_book_id_label = new javax.swing.JLabel();
        Lib_book_name_label = new javax.swing.JLabel();
        Lib_cate_label = new javax.swing.JLabel();
        Lib_id_text = new app.bolivia.swing.JCTextField();
        Lib_bookName_text = new app.bolivia.swing.JCTextField();
        Lib_timKiem_button = new javax.swing.JButton();
        Lib_toanBo_button = new javax.swing.JButton();
        Lib_author_text = new app.bolivia.swing.JCTextField();
        Lib_author_label = new javax.swing.JLabel();
        Lib_publisher_label = new javax.swing.JLabel();
        Lib_publisher_text = new app.bolivia.swing.JCTextField();
        Lib_quantity_label = new javax.swing.JLabel();
        Lib_muonSach_button = new javax.swing.JButton();
        Lib_cate_text = new app.bolivia.swing.JCTextField();
        Lib_quantity_consttext = new app.bolivia.swing.JCTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        home_page_panel.setBackground(new java.awt.Color(186, 221, 255));
        home_page_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        librarypage_panel.setBackground(new java.awt.Color(186, 221, 255));

        Lib_view_book_ljscroll.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "BookID", "BookName", "Category_ID", "Author_ID", "Publisher_ID", "Quantity"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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
        Lib_view_book_ljscroll.setColorFilasBackgound2(new java.awt.Color(255, 255, 255));
        Lib_view_book_ljscroll.setRowHeight(50);
        Lib_view_book_ljscroll.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        Lib_view_book_ljscroll.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                Lib_view_book_ljscrollFocusGained(evt);
            }
        });
        Lib_view_book_jscrollPane.setViewportView(Lib_view_book_ljscroll);

        library_label.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        library_label.setText("Library");

        Lib_book_id_label.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        Lib_book_id_label.setText("Book_ID :");

        Lib_book_name_label.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        Lib_book_name_label.setText("Book_Name :");

        Lib_cate_label.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        Lib_cate_label.setText("Category_ID :");

        Lib_id_text.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Lib_id_text.setPlaceholder("ID . . .");

        Lib_bookName_text.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Lib_bookName_text.setPlaceholder("Name . . .");

        Lib_timKiem_button.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Lib_timKiem_button.setText("Tìm kiếm");
        Lib_timKiem_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Lib_timKiem_buttonActionPerformed(evt);
            }
        });

        Lib_toanBo_button.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Lib_toanBo_button.setText("Toàn bộ");
        Lib_toanBo_button.setToolTipText("");
        Lib_toanBo_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Lib_toanBo_buttonActionPerformed(evt);
            }
        });

        Lib_author_text.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Lib_author_text.setPlaceholder("Author ID . . .");

        Lib_author_label.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        Lib_author_label.setText("Author_ID :");

        Lib_publisher_label.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        Lib_publisher_label.setText("Publisher ID :");

        Lib_publisher_text.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Lib_publisher_text.setPlaceholder("Publicher ID . . .");

        Lib_quantity_label.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        Lib_quantity_label.setText("Quantity : ");

        Lib_muonSach_button.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Lib_muonSach_button.setText("Mượn sách");
        Lib_muonSach_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Lib_muonSach_buttonActionPerformed(evt);
            }
        });

        Lib_cate_text.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Lib_cate_text.setPlaceholder("Category  ID . . .");

        Lib_quantity_consttext.setText("0");
        Lib_quantity_consttext.setEnabled(false);
        Lib_quantity_consttext.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Lib_quantity_consttext.setPlaceholder("0");

        javax.swing.GroupLayout library_panelLayout = new javax.swing.GroupLayout(library_panel);
        library_panel.setLayout(library_panelLayout);
        library_panelLayout.setHorizontalGroup(
            library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(library_panelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Lib_book_name_label)
                    .addComponent(Lib_cate_label)
                    .addGroup(library_panelLayout.createSequentialGroup()
                        .addComponent(library_label)
                        .addGap(45, 45, 45)
                        .addComponent(Lib_book_id_label)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Lib_id_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Lib_bookName_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Lib_cate_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(library_panelLayout.createSequentialGroup()
                        .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(Lib_publisher_label)
                            .addComponent(Lib_quantity_label)
                            .addComponent(Lib_author_label))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Lib_author_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Lib_publisher_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Lib_quantity_consttext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(41, 41, 41))
                    .addGroup(library_panelLayout.createSequentialGroup()
                        .addComponent(Lib_toanBo_button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Lib_timKiem_button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Lib_muonSach_button)
                        .addGap(4, 4, 4))))
        );
        library_panelLayout.setVerticalGroup(
            library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(library_panelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, library_panelLayout.createSequentialGroup()
                        .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(library_panelLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Lib_id_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Lib_book_id_label)))
                            .addComponent(library_label))
                        .addGap(18, 18, 18)
                        .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Lib_bookName_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Lib_book_name_label)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, library_panelLayout.createSequentialGroup()
                        .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Lib_author_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Lib_author_label))
                        .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(library_panelLayout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addComponent(Lib_publisher_label))
                            .addGroup(library_panelLayout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addComponent(Lib_publisher_text, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(34, 34, 34)
                        .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Lib_quantity_label)
                            .addComponent(Lib_cate_text, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Lib_cate_label)
                            .addComponent(Lib_quantity_consttext, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Lib_toanBo_button, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Lib_muonSach_button)
                        .addComponent(Lib_timKiem_button, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout librarypage_panelLayout = new javax.swing.GroupLayout(librarypage_panel);
        librarypage_panel.setLayout(librarypage_panelLayout);
        librarypage_panelLayout.setHorizontalGroup(
            librarypage_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, librarypage_panelLayout.createSequentialGroup()
                .addGroup(librarypage_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Lib_view_book_jscrollPane, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(library_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        librarypage_panelLayout.setVerticalGroup(
            librarypage_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, librarypage_panelLayout.createSequentialGroup()
                .addComponent(library_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Lib_view_book_jscrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        home_page_panel.add(librarypage_panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 880, 670));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 873, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(home_page_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 873, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 668, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(home_page_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 668, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Lib_toanBo_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Lib_toanBo_buttonActionPerformed
        // TODO add your handling code here:
            Library_greetingLoad();
            Library_Load();
    }//GEN-LAST:event_Lib_toanBo_buttonActionPerformed

    private void Lib_timKiem_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Lib_timKiem_buttonActionPerformed
        // TODO add your handling code here:
            Library_Load();
    }//GEN-LAST:event_Lib_timKiem_buttonActionPerformed

    private void Lib_muonSach_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Lib_muonSach_buttonActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_Lib_muonSach_buttonActionPerformed

    private void Lib_view_book_ljscrollFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_Lib_view_book_ljscrollFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_Lib_view_book_ljscrollFocusGained

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
            java.util.logging.Logger.getLogger(Library.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Library.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Library.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Library.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Library("user").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Lib_author_label;
    private app.bolivia.swing.JCTextField Lib_author_text;
    private app.bolivia.swing.JCTextField Lib_bookName_text;
    private javax.swing.JLabel Lib_book_id_label;
    private javax.swing.JLabel Lib_book_name_label;
    private javax.swing.JLabel Lib_cate_label;
    private app.bolivia.swing.JCTextField Lib_cate_text;
    private app.bolivia.swing.JCTextField Lib_id_text;
    private javax.swing.JButton Lib_muonSach_button;
    private javax.swing.JLabel Lib_publisher_label;
    private app.bolivia.swing.JCTextField Lib_publisher_text;
    private app.bolivia.swing.JCTextField Lib_quantity_consttext;
    private javax.swing.JLabel Lib_quantity_label;
    private javax.swing.JButton Lib_timKiem_button;
    private javax.swing.JButton Lib_toanBo_button;
    private javax.swing.JScrollPane Lib_view_book_jscrollPane;
    private rojeru_san.complementos.RSTableMetro Lib_view_book_ljscroll;
    private javax.swing.JPanel home_page_panel;
    private javax.swing.JLabel library_label;
    private javax.swing.JPanel library_panel;
    private javax.swing.JPanel librarypage_panel;
    // End of variables declaration//GEN-END:variables
}
