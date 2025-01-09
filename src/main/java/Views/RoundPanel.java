package Views;

import DAOs.TournoiDAO;
import Observer.IObserver;
import Utils.AppContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class RoundPanel extends JPanel implements IObserver {
    private final int tournoiId;  // The ID of the tournament to fetch round data for
    private JTable roundDataTable;

    public RoundPanel(int tournoiId) {
        this.tournoiId = tournoiId;
        setLayout(new BorderLayout(10, 10));

        // Register this panel as an observer
        AppContext.addObserver(this);

        // Header panel
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Table for round data
        add(createRoundDataTable(), BorderLayout.CENTER);

        // Fetch and display round data
        refreshRoundData();
    }

    @Override
    public void update() {
        // Refresh round data when the update method is called by the observed object
        refreshRoundData();
    }

    // Create header for the round data panel
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        JLabel header = new JLabel("Round Data", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(header, BorderLayout.CENTER);
        return headerPanel;
    }

    // Create JTable to display round data
    private JScrollPane createRoundDataTable() {
        String[] columnNames = {"Round", "Total Matches", "Played Games"};
        Object[][] data = {};  // Empty data initially

        roundDataTable = new JTable(new javax.swing.table.DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Disable editing for all columns
                return false;
            }
        });

        roundDataTable.setFillsViewportHeight(true);
        
        // Add mouse listener to redirect to match management panel on row selection
        roundDataTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double click detected
                    int selectedRow = roundDataTable.getSelectedRow();
                    if (selectedRow != -1) {
                        int roundId = (int) roundDataTable.getValueAt(selectedRow, 0);
                        redirectToMatchManagementPanel(roundId);
                    }
                }
            }
        });

        return new JScrollPane(roundDataTable);
    }

    // Fetch round data from the database and update the table
    private void refreshRoundData() {
        if (AppContext.getCurrentTournament().getId() == -1) {
            roundDataTable.setModel(new javax.swing.table.DefaultTableModel(new Object[][]{}, new String[] {"Round", "Total Matches", "Played Games"}));
        }
        try {
            // Fetch round data from DAO
            List<int[]> roundData = new TournoiDAO().getRoundData(tournoiId);

            // Prepare data for the table
            Object[][] tableData = new Object[roundData.size()][3];
            for (int i = 0; i < roundData.size(); i++) {
                int[] data = roundData.get(i);
                tableData[i][0] = data[0]; // Round number
                tableData[i][1] = data[1]; // Total matches
                tableData[i][2] = data[2]; // Played games
                System.out.println(data[0] + " " + data[1] + "  " + tableData[i][2]);
            }

            // Update the table model with the new data
            roundDataTable.setModel(new javax.swing.table.DefaultTableModel(tableData, new String[] {"Round", "Total Matches", "Played Games"}));
        } catch (Exception e) {
            // Show error message and log exception
            JOptionPane.showMessageDialog(this, "Failed to load round data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Redirect to match management panel
    private void redirectToMatchManagementPanel(int roundId) {
        // Implementation for redirecting to match management panel goes here
        System.out.println("Redirecting to Match Management Panel for Round ID: " + roundId);
        // Example: new MatchManagementPanel(roundId);
    }
}
