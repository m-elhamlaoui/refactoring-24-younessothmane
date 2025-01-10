package Views;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;
import java.util.Vector;

import DAOs.FactoryDAO;
import DAOs.TournoiDAO;
import Strategy.*;
import Utils.AppContext;
import Observer.IObserver;

public class TournamentSelectionPanel extends JPanel implements IObserver {
    private final MainFrame frame;
    private final ResourceBundle messages;
    private JList<String> tournamentList;
    private JButton selectButton;
    private JButton deleteButton;

    public TournamentSelectionPanel(MainFrame frame, ResourceBundle messages) {
        this.frame = frame;
        this.messages = messages;
        initializeUI();
        refreshTournamentList();
        AppContext.addObserver(this);  // Register as an observer
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));  // Soft background color
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Tournament List Panel
        JPanel listPanel = createTournamentListPanel();
        add(listPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel header = new JLabel(getLocalizedString("TOURNOI_LIST"));
        header.setFont(new Font("Arial", Font.BOLD, 18));
        header.setForeground(new Color(0, 0, 0)); // Dark text color
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        headerPanel.setBackground(new Color(220, 220, 220));  // Light gray background
        headerPanel.add(header, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createTournamentListPanel() {
        JPanel listPanel = new JPanel(new BorderLayout());
        tournamentList = new JList<>();
        tournamentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tournamentList.addListSelectionListener(e -> updateButtonStates());

        JScrollPane scrollPane = new JScrollPane(tournamentList);
        scrollPane.setPreferredSize(new Dimension(350, 200));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        listPanel.setBackground(new Color(245, 245, 245));  // Same as panel background
        listPanel.add(scrollPane, BorderLayout.CENTER);
        return listPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));  // Same as panel background
        
        JButton createButton = createStyledButton(getLocalizedString("CREER_TOURNOI"));
        selectButton = createStyledButton(getLocalizedString("SELECT_TOURNAMENT"));
        deleteButton = createStyledButton(getLocalizedString("SUPPRIMER_TOURNOI"));

        createButton.addActionListener(e -> handleCreateTournament());
        selectButton.addActionListener(e -> handleSelectTournament());
        deleteButton.addActionListener(e -> handleDeleteTournament());

        // Disable select and delete buttons initially
        selectButton.setEnabled(false);
        deleteButton.setEnabled(false);

        buttonPanel.add(createButton);
        buttonPanel.add(selectButton);
        buttonPanel.add(deleteButton);

        return buttonPanel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 35));
        button.setBackground(new Color(0, 123, 255));  // Blue background
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));  // Hand cursor on hover
        return button;
    }

    private void handleCreateTournament() {
        String tournamentName = showTournamentNameDialog();
        if (tournamentName != null && !tournamentName.trim().isEmpty()) {
            Boolean ok = new CreateTournamentStrategy(tournamentName).execute();
            if (ok) {
                refreshTournamentList();
            } else {
                showErrorMessage(getLocalizedString("TOURNAMENT_CREATION_ERROR"));
            }
        }
    }

    private String showTournamentNameDialog() {
        return (String) JOptionPane.showInputDialog(
                null,
                "Enter the tournament name:",
                "Tournament Name",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private void handleSelectTournament() {
        String selectedTournament = tournamentList.getSelectedValue();
        if (selectedTournament != null) {
            Boolean ok = new SelectTournamentStrategy(frame, selectedTournament).execute();
            if (!ok) {
                this.showErrorMessage("Tournament not found!");
            }
        }
    }

    private void handleDeleteTournament() {
        String selectedTournament = tournamentList.getSelectedValue();
        if (selectedTournament != null) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    getLocalizedString("CONFIRM_DELETE_MESSAGE"),
                    getLocalizedString("CONFIRM_DELETE_TITLE"),
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                Boolean ok = new DeleteTournamentStrategy(selectedTournament).execute();
                if (ok) {
                    refreshTournamentList();
                    showSuccessMessage(getLocalizedString("TOURNAMENT_DELETED_SUCCESS"));
                } else {
                    showErrorMessage(getLocalizedString("TOURNAMENT_DELETION_ERROR"));
                }
            }
        }
    }

    private void refreshTournamentList() {
        try {
            TournoiDAO dao = FactoryDAO.getTournoiDAO();
            Vector<String> names = dao.getTournoiNames();
            tournamentList.setListData(names);
            updateButtonStates();
        } catch (Exception ex) {
            showErrorMessage(getLocalizedString("TOURNAMENT_LOAD_ERROR"));
        }
    }

    private void updateButtonStates() {
        boolean hasSelection = tournamentList.getSelectedValue() != null;
        selectButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
    }

    @Override
    public void update() {
        refreshTournamentList();  // Refresh the tournament list when notified of changes
    }

    private String getLocalizedString(String key) {
        try {
            return messages.getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                getLocalizedString("ERROR_TITLE"),
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                getLocalizedString("SUCCESS_TITLE"),
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
