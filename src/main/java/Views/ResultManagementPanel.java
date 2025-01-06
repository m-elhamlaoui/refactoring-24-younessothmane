package Views;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

import DAOs.FactoryDAO;
import DAOs.MatchDAO;
import Models.Match;
import Utils.AppContext;

public class ResultManagementPanel extends JPanel {
    private final MainFrame frame;
    private final ResourceBundle messages;

    public ResultManagementPanel(MainFrame frame, ResourceBundle messages) {
        this.frame = frame;
        this.messages = messages;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JLabel header = new JLabel(messages.getString("RESULTATS"), JLabel.CENTER);
        add(header, BorderLayout.NORTH);

        JTable resultTable = new JTable(getResultData(), getResultColumns());
        JScrollPane scrollPane = new JScrollPane(resultTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private Object[][] getResultData() {
        MatchDAO dao = FactoryDAO.getMatchDAO();
        return dao.getMatchsByTournoi(AppContext.getCurrentTournament().getId())
                  .stream()
                  .filter(Match::isTermine)
                  .map(m -> new Object[]{
                      m.getEq1(),
                      m.getEq2(),
                      m.getScore1(),
                      m.getScore2()
                  })
                  .toArray(Object[][]::new);
    }

    private String[] getResultColumns() {
        return new String[]{
            messages.getString("EQ1"),
            messages.getString("EQ2"),
            messages.getString("S_EQ1"),
            messages.getString("S_EQ2")
        };
    }
}
