
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Board 
{
	Row[] rows = new Row[8];
	public Board()
	{
		for(int i = 0; i < 8; i++)
		{
			rows[i] = new Row(i);
		}
	}
	
	public Row getRow(int rowNum)
	{
		return rows[rowNum];
	}
	
	public Row[] getRows()
	{
		return rows;
	}
	
	//called only when board has at least one queen placed on it
	public boolean isValid()
	{
		int lastOccupiedRow = 0;
		int lastOccupiedColumn = 0;
		
		//find last occupied row
		for(Row row : getRows())
		{
			if(row.isEmpty())
			{
				lastOccupiedRow = row.getRowNum() - 1;
				break;
			}
			if(row.getRowNum() == 7 && (!row.isEmpty()))
			{
				lastOccupiedRow = 7;
				break;
			}
		}
		
		if(lastOccupiedRow == 0)
		{
			return true;
		}
		
		lastOccupiedColumn = getRow(lastOccupiedRow).getOccupiedColumn();
		
		//check all rows prior to last occupied row for matching column
		for(int i = 0; i < lastOccupiedRow; i++)
		{
			if(getRow(i).getOccupiedColumn() == getRow(lastOccupiedRow).getOccupiedColumn())
			{
				return false;
			}
		}
		
		//check  diagonals by going up one row at a time from the last occupied row. For each row moved up, go one column to left or right and check if
		//that row's occupied square is one of those columns.
		
		int rowsAboveLastOccupiedRow = 1;
		for(int i = lastOccupiedRow - 1; i >= 0; i--)
		{
			int leftColumnOnDiagonal = lastOccupiedColumn - rowsAboveLastOccupiedRow;
			int rightColumnOnDiagonal = lastOccupiedColumn + rowsAboveLastOccupiedRow;
			
			if(getRow(i).getOccupiedColumn() == leftColumnOnDiagonal || getRow(i).getOccupiedColumn() == rightColumnOnDiagonal)
			{
				return false;
			}
			
			++rowsAboveLastOccupiedRow;
		}
		
		if (lastOccupiedRow == 7) {
			printBoard();
		}
		
		return true; 
	}
	
	private void printBoard() 
	{
		new SolutionBoard(getRows());
	}

	public void placeQueen(int row, int col)
	{
		this.getRow(row).placeQueen(col);
		//if board is valid, place queen on next row, first column, if any rows left.
		if(isValid())
		{
			//System.out.println("Placed queen in row " + row + " column " + col);
			if(row == 7)
			{
				//we're done
				return;
			}
			else
			{
				//go to next row
				placeQueen(row + 1, 0);
			}
		}
		else
		{
			//board not valid. Move one column to right if room
			if(col < 7)
			{
				removeQueen(row,col);
				placeQueen(row, col + 1);
			}
			else
			{
				//reached end of column. remove queen and go up a row.
				removeQueen(row,col);
				int prevRow = row - 1;
				while(getRow(prevRow).getOccupiedColumn() == 7)
				{
					removeQueen(prevRow, 7);
					--prevRow;
				}
				//now move queen in this row to the right by one column
				int currRow = prevRow;
				int currentOccupiedColumn = getRow(currRow).getOccupiedColumn();
				removeQueen(currRow, currentOccupiedColumn);
				placeQueen(currRow, currentOccupiedColumn + 1);
			}
		}
	}
	
	
	private void removeQueen(int row, int col) 
	{
		getRow(row).removeQueen(col);
		
	}

	public static void main(String[] args)
	{
		Board theBoard = new Board();
		theBoard.placeQueen(0,0);
	}
}

class Row
{
	private Square[] squares = new Square[8];
	private int rowNum;
	
	public int getRowNum()
	{
		return rowNum;
	}
	
	public void removeQueen(int col) 
	{
		squares[col].removeQueen();
	}

	public void placeQueen(int col) 
	{
		squares[col].placeQueen();
		
	}

	public int getOccupiedColumn()
	{
		for(Square square : squares)
		{
			if(square.isOccupied())
			{
				return square.getColumnNumber();
			}
		}
		return -1;
	}
	
	public boolean isEmpty()
	{
		for(Square square : squares)
		{
			if(square.isOccupied())
			{
				return false;
			}
		}
		return true;
	}
	
	public Row(int p_rowNum)
	{
		rowNum = p_rowNum;
		for(int i = 0; i < 8; i++)
		{
			squares[i] = new Square(i);
		}
	}
}

class Square
{
	private boolean occupied;
	private int columnNumber;
	
	public Square(int column)
	{
		columnNumber = column;
	}
	
	public void removeQueen() 
	{
		occupied = false;
	}

	public void placeQueen() 
	{
		occupied = true;
	}

	public int getColumnNumber()
	{
		return columnNumber;
	}
	
	public boolean isOccupied()
	{
		return occupied;
	}
}

@SuppressWarnings("serial")
class SolutionBoard extends JFrame
{
	JPanel contentPanel = new JPanel(new GridLayout(8,8));
	boolean white = true;
	ImageIcon whiteBG = null;
	ImageIcon blackBG = null;

	public SolutionBoard(final Row[] p_rows)
	{
		this.setTitle("Eight Queens Solution");
		BufferedImage imageBlackBg = null;
		BufferedImage imageWhiteBg = null;
		
		try {
			imageBlackBg = ImageIO.read(getClass().getResource("/images/icon40black.jpg"));
			blackBG = new ImageIcon(imageBlackBg);
			
			imageWhiteBg = ImageIO.read(getClass().getResource("/images/icon40white.jpg"));
			whiteBG = new ImageIcon(imageWhiteBg);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i = 0; i < 8; i ++)
		{
			white = !white;
			for(int j = 0; j < 8; j++)
			{
				JButton button = new JButton();
				contentPanel.add(button);
				button.setBackground(white ? new Color(255,255,255) : new Color(0,0,0));
				
				if(p_rows[i].getOccupiedColumn() == j)
				{
					button.setIcon(white ? whiteBG : blackBG );
				}
				white = !white;
			}
		}
		setContentPane(contentPanel);
		pack();
		this.setLocation(300,300);
		this.setMinimumSize(new Dimension(360,540));
		setVisible(true);
		
		Container myContainer = this.getContentPane();
		Component[] components = myContainer.getComponents();
		for(Component component : components)
		{
			((JButton)component).setFocusPainted(false);
		}
	}
	
	
}

