import com.company.*;
import com.google.common.base.Joiner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


public class IsGUI {
    private JButton buttonAdd;
    private JButton buttonOpenFile;
    private JButton buttonSavaToDb;
    private JButton buttonSaveToFile;
    private JButton buttonSaveToXml;
    private JTable mainTable;
    private JPanel appPanel;
    private JScrollPane mainScrollPane;
    private JButton buttonLoadDb;
    private JButton buttonOpenXmlFile;

    private DefaultTableModel dataTableModel;

    final private JFileChooser fileChooser = new JFileChooser();
    final private FileNameExtensionFilter txtFilter = new FileNameExtensionFilter("*.txt", "txt");
    final private FileNameExtensionFilter xmlFilter = new FileNameExtensionFilter("*.xml", "xml");

    final private TxtFileReader textFileReader = new TxtFileReader();
    final private TxtPaser txtParser = new TxtPaser();

    final private Logger logger = Logger.getAnonymousLogger();

    private final Integer EDITED_HIDDEN_COLIDX = 15;
    private final Boolean DEFAULT_EDITED = false;
    private static final String[] COLUMN_NAMES = {
            "Producent",
            "Wielkość matrycy",
            "Rozdzielczość",
            "Powłoka matrycy",
            "Ekran dotykowy",
            "Seria procesora",
            "Liczba rdzeni",
            "Taktowanie bazowe",
            "Wielkość pamięci RAM",
            "Pojemność dysku",
            "Typ dysku",
            "Karta graficzna",
            "Pamięć karty graficznej",
            "System operacyjny",
            "Napęd optyczny",
            "edited_hidden"
    };

    public IsGUI() {
        dataTableModel = (DefaultTableModel) mainTable.getModel();
        createColumns();
        initGUIComponents();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("LaptopView");
        frame.setContentPane(new IsGUI().appPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    private List<DataModel> findLaptopDuplicates(DataModel dataToCheck) {
        List<DataModel> duplicates = new ArrayList<>();
        List<DataModel> laptopsFromTableModel = txtParser.parseVector(dataTableModel.getDataVector());
        for (DataModel laptopFromTableModel : laptopsFromTableModel) {
            if (laptopFromTableModel.equals(dataToCheck)) {
                duplicates.add(laptopFromTableModel);
            }
        }
        return duplicates;
    }

    private void createColumns() {
        for (String columnName : COLUMN_NAMES) {
            dataTableModel.addColumn(columnName);
        }
    }

    private void initGUIComponents() {
        mainTable.removeColumn(mainTable.getColumnModel().getColumn(EDITED_HIDDEN_COLIDX));

        mainTable.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    if (!(Boolean) dataTableModel.getValueAt(e.getFirstRow(), EDITED_HIDDEN_COLIDX)) {
                        dataTableModel.setValueAt(true, e.getFirstRow(), EDITED_HIDDEN_COLIDX);
                    }
                    mainTable.repaint();
                }
                logger.log(Level.INFO, e.toString());
            }
        });

        mainTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
/*
                DataModel rowLaptop = txtParser.parse((Vector) dataTableModel.getDataVector().get(row));

                if ((Boolean) dataTableModel.getValueAt(row, EDITED_HIDDEN_COLIDX)) {
                    cellComponent.setBackground(new Color(255, 255, 224));
                } else if (findLaptopDuplicates(rowLaptop).size() > 1) {
                    cellComponent.setBackground(new java.awt.Color(255, 72, 72));
                } else {
                    cellComponent.setBackground(new Color(185, 185, 158));
                }

 */
                return cellComponent;
            }
        });

        buttonAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        buttonOpenFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                readFromTxtFile();
            }
        });

        buttonSaveToFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToTextFile();
            }
        });

        buttonSaveToXml.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToDb();
            }
        });

        buttonSavaToDb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToDb();
            }
        });

        buttonLoadDb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    loadDb();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    private void readFromTxtFile() {
        fileChooser.setFileFilter(txtFilter);
        int returnVal = fileChooser.showOpenDialog(appPanel);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File txtFile = fileChooser.getSelectedFile();
            IsGUI.this.loadTxtFile(txtFile);
            logger.log(Level.INFO, "Opening: " + txtFile.getName() + ".\n");
        } else {
            logger.log(Level.INFO, "Open command cancelled by user.\n");
        }
    }

    private void loadTxtFile(File file) {
        List<String> fileLines = textFileReader.readLines(file);
        List<DataModel> dataModel = txtParser.parseList(fileLines);

        if (dataModel.size() > 0) {
            fillTableModelWithLaptopData(dataModel);
        }
    }

    private void fillTableModelWithLaptopData(List<DataModel> dataModels) {
        Object[] rowData = new Object[16];
        for (DataModel dataModel : dataModels) {
            rowData[0] = dataModel.getManufacturer();
            rowData[1] = dataModel.getMatrixSize();
            rowData[2] = dataModel.getResolution();
            rowData[3] = dataModel.getMatrixCoating();
            rowData[4] = dataModel.getTouchPad();
            rowData[5] = dataModel.getCpuFamily();
            rowData[6] = dataModel.getCoresCount();
            rowData[7] = dataModel.getClockSpeed();
            rowData[8] = dataModel.getRam();
            rowData[9] = dataModel.getDriveCapacity();
            rowData[10] = dataModel.getDriveType();
            rowData[11] = dataModel.getGpu();
            rowData[12] = dataModel.getGpuMemory();
            rowData[13] = dataModel.getOs();
            rowData[14] = dataModel.getOpticalDrive();
            rowData[15] = DEFAULT_EDITED;
            dataTableModel.addRow(rowData);
        }
    }

    private void saveToTextFile() {
        Vector dataVector = dataTableModel.getDataVector();
        List<String> lines = new ArrayList<>();

        for (Object element : dataVector) {
            lines.add(Joiner.on(";").join((List<String>) element) + ";");
        }

        fileChooser.setFileFilter(txtFilter);
        int returnVal = fileChooser.showSaveDialog(appPanel);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File txtFileToSave = fileChooser.getSelectedFile();
            if (FilenameUtils.getExtension(txtFileToSave.getName()).equalsIgnoreCase("txt")) {
                // dobre rozszerzenie
            } else {
                txtFileToSave = new File(txtFileToSave.toString() + ".txt");
                txtFileToSave = new File(txtFileToSave.getParentFile(),
                        FilenameUtils.getBaseName(txtFileToSave.getName()) + ".txt");
            }
            try {
                FileUtils.writeStringToFile(txtFileToSave, Joiner.on("\n").join(lines));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            logger.log(Level.INFO, "Saving: " + txtFileToSave.getName() + ".\n");
        } else {
            logger.log(Level.INFO, "Save command cancelled by user.\n");
        }
    }

    private void saveToDb() {
        Database db = new Database();
        db.createConnection();
        DefaultTableModel daDefaultTableModel = (DefaultTableModel) mainTable.getModel();
        for (int row = 0; row < mainTable.getRowCount(); row++) {
            String query = "CALL saveLaptopy( ";

            for (int col = 0; col < mainTable.getColumnCount(); col++) {
                if(col==mainTable.getColumnCount()-1){
                    if("".equals(mainTable.getValueAt(row, col))){
                        query += "null";
                    }else{
                        query += "\""+ mainTable.getValueAt(row, col) +"\"";
                    }
                }else {
                    if ("".equals(mainTable.getValueAt(row, col))) {
                        query += "null,";
                    } else {
                        query +="\""+ mainTable.getValueAt(row, col) +"\"" + ",";
                    }
                }
            }
            query +=");";
            db.insertRecord(query);

        }
        db.closeConnection();
    }

    private void loadDb() throws SQLException {
        Database db = new Database();
        db.createConnection();
        ArrayList<DataModel> nowaListaLaptopow = new ArrayList<>();
        String query = "CALL `openLaptopy`();";
        ResultSet rs = db.prepareStatement(query);

            createLaptopFromResponse(rs);
    }

    private void createLaptopFromResponse(ResultSet res) throws SQLException {
        DataModel l = new DataModel();
        dataTableModel.setRowCount(0);
        while (res.next()){
            Object manufacturer = res.getObject("manufacturer");
            Object screenSize = res.getObject("screenSize");
            Object screenResolution = res.getObject("resolution");
            Object screenType = res.getObject("screenCoating");
            Object touchscreen = res.getObject("touchPad");
            Object processor_name = res.getObject("cpu");
            Object physical_cores = res.getObject("coreCount");
            Object clock_speed = res.getObject("clockSpeed");
            Object ram = res.getObject("ram");
            Object disk_storage = res.getObject("driveCapacity");
            Object disk_type = res.getObject("driveType");
            Object graphic_card_name = res.getObject("gpu");
            Object graphic_card_memory = res.getObject("gpuRam");
            Object operating_system = res.getObject("os");
            Object disc_reader = res.getObject("opticalDrive");
            Object a[]=new Object[16];

            a[0]= manufacturer;
            a[1]= screenSize;
            a[2]=screenResolution;
            a[3]=screenType;
            a[4]=touchscreen;
            a[5]=processor_name;
            a[6] = physical_cores;
            a[7] = clock_speed;
            a[8]= ram;
            a[9]= disk_storage;
            a[10]= disk_type;
            a[11]= graphic_card_name;
            a[12]= graphic_card_memory;
            a[13]=operating_system;
            a[14]=disc_reader;
            dataTableModel.addRow(a);
        }
    }

}
