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
                    con = DriverManager.getConnection("jdbc:mysql://localhost/" + Database.DB_Name,Database.DB_UserName,Database.DB_Password);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
//---------------------------------------------------------------------------------------------------
//  *** Search Page
//  0. Load greetingLibraryPage
    private void Library_greetingLoad(){
        id_text_LIB.setText("");
        bookName_text_LIB.setText("");
        cate_text.setText("");
        author_text_LIB.setText("");
        publisher_text_LIB.setText("");
        quantity_consttext_LIB.setText("0");
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
            pst.setString(1, "%" + id_text_LIB.getText() + "%");
            pst.setString(2, "%" + bookName_text_LIB.getText() + "%");
            pst.setString(3, "%" + cate_text.getText() + "%");
            pst.setString(4, "%" + author_text_LIB.getText() + "%");
            pst.setString(5, "%" + publisher_text_LIB.getText() + "%");
            rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel) view_book_library_jscroll.getModel();
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
        toanBo_button_LIB.setEnabled(true);
        timKiem_button_LIB.setEnabled(true);
        if(quantity_consttext_LIB.getText().equals("0") == true){
            muonSach_button_LIB.setEnabled(false);
        }
        else{
        muonSach_button_LIB.setEnabled(true);
        }
    }

    //  1.2.2 Load text od search panel
    private void Library_textLoad() {
        String sql_cmd = " SELECT books.Quantity "
                + " FROM books "
                + " WHERE books.Book_ID LIKE ? AND books.Name LIKE ? AND books.Category_ID LIKE ? AND books.Author_ID LIKE ? AND books.Publisher_ID LIKE ? ";
        try {
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1, "%" + id_text_LIB.getText() + "%");
            pst.setString(2, "%" + bookName_text_LIB.getText() + "%");
            pst.setString(3, "%" + cate_text.getText() + "%");
            pst.setString(4, "%" + author_text_LIB.getText() + "%");
            pst.setString(5, "%" + publisher_text_LIB.getText() + "%");
            rs = pst.executeQuery();
            rs.next();
            quantity_consttext_LIB.setText(rs.getString(1));
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
            pst.setString(1, "%" + id_text_LIB.getText() + "%");
            pst.setString(2, "%" + bookName_text_LIB.getText() + "%");
            pst.setString(3, "%" + cate_text.getText() + "%");
            pst.setString(4, "%" + author_text_LIB.getText() + "%");
            pst.setString(5, "%" + publisher_text_LIB.getText() + "%");
            rs = pst.executeQuery();
            rs.next();
            quantity_consttext_LIB.setText(rs.getString(1));
            
        } catch (SQLException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//******************************************************************************
    //  *** Return Book 
//  1. Check What Row is chosen
    private int indexOfRow() {// trả về thứ tự của dòng trong bảng bắt đầu từ 0, nếu không có dòng nào được chọn thì trả về - 1
        return view_book_library_jscroll.getSelectedRow();
    }
    //  2. Return Book
    private boolean returnBook() {
        int indexBook = indexOfRow();

        if (indexOfRow() == -1 || view_book_library_jscroll.getValueAt(indexBook, 4).equals("Returned")) {
            return false;
        }

        String sql_cmd = "UPDATE issuebooks SET issuebooks.Status = ? WHERE issuebooks.Book_ID = ? ";
        try {
            pst = con.prepareStatement(sql_cmd);
            pst.setString(1, "Returned");
            pst.setString(2, (String) view_book_library_jscroll.getValueAt(indexBook, 0));
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
        view_library_book_jscroll = new javax.swing.JScrollPane();
        view_book_library_jscroll = new rojeru_san.complementos.RSTableMetro();
        library_panel = new javax.swing.JPanel();
        library_label = new javax.swing.JLabel();
        book_id_label_LIB = new javax.swing.JLabel();
        book_name_label_LIB = new javax.swing.JLabel();
        cate_label_LIB = new javax.swing.JLabel();
        id_text_LIB = new app.bolivia.swing.JCTextField();
        bookName_text_LIB = new app.bolivia.swing.JCTextField();
        timKiem_button_LIB = new javax.swing.JButton();
        toanBo_button_LIB = new javax.swing.JButton();
        author_text_LIB = new app.bolivia.swing.JCTextField();
        author_label_LIB = new javax.swing.JLabel();
        publisher_label_LIB = new javax.swing.JLabel();
        publisher_text_LIB = new app.bolivia.swing.JCTextField();
        quantity_label_LIB = new javax.swing.JLabel();
        muonSach_button_LIB = new javax.swing.JButton();
        cate_text = new app.bolivia.swing.JCTextField();
        quantity_consttext_LIB = new app.bolivia.swing.JCTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        home_page_panel.setBackground(new java.awt.Color(186, 221, 255));
        home_page_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        librarypage_panel.setBackground(new java.awt.Color(186, 221, 255));

        view_book_library_jscroll.setModel(new javax.swing.table.DefaultTableModel(
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
        view_book_library_jscroll.setColorFilasBackgound2(new java.awt.Color(255, 255, 255));
        view_book_library_jscroll.setRowHeight(50);
        view_book_library_jscroll.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        view_book_library_jscroll.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                view_book_library_jscrollFocusGained(evt);
            }
        });
        view_library_book_jscroll.setViewportView(view_book_library_jscroll);

        library_label.setText("Library");
        library_label.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N

        book_id_label_LIB.setText("Book_ID :");
        book_id_label_LIB.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        book_name_label_LIB.setText("Book_Name :");
        book_name_label_LIB.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        cate_label_LIB.setText("Category_ID :");
        cate_label_LIB.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        id_text_LIB.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        id_text_LIB.setPlaceholder("ID . . .");

        bookName_text_LIB.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        bookName_text_LIB.setPlaceholder("Name . . .");

        timKiem_button_LIB.setText("Tìm kiếm");
        timKiem_button_LIB.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        timKiem_button_LIB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timKiem_button_LIBActionPerformed(evt);
            }
        });

        toanBo_button_LIB.setText("Toàn bộ");
        toanBo_button_LIB.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        toanBo_button_LIB.setToolTipText("");
        toanBo_button_LIB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toanBo_button_LIBActionPerformed(evt);
            }
        });

        author_text_LIB.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        author_text_LIB.setPlaceholder("Author ID . . .");

        author_label_LIB.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        author_label_LIB.setText("Author_ID :");

        publisher_label_LIB.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        publisher_label_LIB.setText("Publisher ID :");

        publisher_text_LIB.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        publisher_text_LIB.setPlaceholder("Publicher ID . . .");

        quantity_label_LIB.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        quantity_label_LIB.setText("Quantity : ");

        muonSach_button_LIB.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        muonSach_button_LIB.setText("Mượn sách");
        muonSach_button_LIB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                muonSach_button_LIBActionPerformed(evt);
            }
        });

        cate_text.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cate_text.setPlaceholder("Category  ID . . .");

        quantity_consttext_LIB.setText("0");
        quantity_consttext_LIB.setEnabled(false);
        quantity_consttext_LIB.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        quantity_consttext_LIB.setPlaceholder("0");

        javax.swing.GroupLayout library_panelLayout = new javax.swing.GroupLayout(library_panel);
        library_panel.setLayout(library_panelLayout);
        library_panelLayout.setHorizontalGroup(
            library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(library_panelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(book_name_label_LIB)
                    .addComponent(cate_label_LIB)
                    .addGroup(library_panelLayout.createSequentialGroup()
                        .addComponent(library_label)
                        .addGap(45, 45, 45)
                        .addComponent(book_id_label_LIB)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(id_text_LIB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bookName_text_LIB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cate_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(library_panelLayout.createSequentialGroup()
                        .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(publisher_label_LIB)
                            .addComponent(quantity_label_LIB)
                            .addComponent(author_label_LIB))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(author_text_LIB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(publisher_text_LIB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(quantity_consttext_LIB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(41, 41, 41))
                    .addGroup(library_panelLayout.createSequentialGroup()
                        .addComponent(toanBo_button_LIB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(timKiem_button_LIB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(muonSach_button_LIB)
                        .addGap(4, 4, 4))))
        );
        library_panelLayout.setVerticalGroup(
            library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(library_panelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(library_panelLayout.createSequentialGroup()
                        .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(library_label)
                            .addGroup(library_panelLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(id_text_LIB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(book_id_label_LIB))))
                        .addGap(18, 18, 18)
                        .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(bookName_text_LIB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(book_name_label_LIB))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(library_panelLayout.createSequentialGroup()
                        .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(author_text_LIB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(author_label_LIB))
                        .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(library_panelLayout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addComponent(publisher_label_LIB))
                            .addGroup(library_panelLayout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addComponent(publisher_text_LIB, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(34, 34, 34)
                        .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(quantity_label_LIB)
                            .addComponent(cate_text, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cate_label_LIB)
                            .addComponent(quantity_consttext_LIB, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)))
                .addGroup(library_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(toanBo_button_LIB, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(muonSach_button_LIB)
                    .addComponent(timKiem_button_LIB, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout librarypage_panelLayout = new javax.swing.GroupLayout(librarypage_panel);
        librarypage_panel.setLayout(librarypage_panelLayout);
        librarypage_panelLayout.setHorizontalGroup(
            librarypage_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, librarypage_panelLayout.createSequentialGroup()
                .addGroup(librarypage_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(view_library_book_jscroll, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(library_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        librarypage_panelLayout.setVerticalGroup(
            librarypage_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, librarypage_panelLayout.createSequentialGroup()
                .addComponent(library_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(view_library_book_jscroll, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void toanBo_button_LIBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toanBo_button_LIBActionPerformed
        // TODO add your handling code here:
            Library_greetingLoad();
            Library_Load();
    }//GEN-LAST:event_toanBo_button_LIBActionPerformed

    private void timKiem_button_LIBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timKiem_button_LIBActionPerformed
        // TODO add your handling code here:
            Library_Load();
    }//GEN-LAST:event_timKiem_button_LIBActionPerformed

    private void muonSach_button_LIBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_muonSach_button_LIBActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_muonSach_button_LIBActionPerformed

    private void view_book_library_jscrollFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_view_book_library_jscrollFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_view_book_library_jscrollFocusGained

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
    private javax.swing.JLabel author_label_LIB;
    private app.bolivia.swing.JCTextField author_text_LIB;
    private app.bolivia.swing.JCTextField bookName_text_LIB;
    private javax.swing.JLabel book_id_label_LIB;
    private javax.swing.JLabel book_name_label_LIB;
    private javax.swing.JLabel cate_label_LIB;
    private app.bolivia.swing.JCTextField cate_text;
    private javax.swing.JPanel home_page_panel;
    private app.bolivia.swing.JCTextField id_text_LIB;
    private javax.swing.JLabel library_label;
    private javax.swing.JPanel library_panel;
    private javax.swing.JPanel librarypage_panel;
    private javax.swing.JButton muonSach_button_LIB;
    private javax.swing.JLabel publisher_label_LIB;
    private app.bolivia.swing.JCTextField publisher_text_LIB;
    private app.bolivia.swing.JCTextField quantity_consttext_LIB;
    private javax.swing.JLabel quantity_label_LIB;
    private javax.swing.JButton timKiem_button_LIB;
    private javax.swing.JButton toanBo_button_LIB;
    private rojeru_san.complementos.RSTableMetro view_book_library_jscroll;
    private javax.swing.JScrollPane view_library_book_jscroll;
    // End of variables declaration//GEN-END:variables
}
