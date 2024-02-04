import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static final int TILE_SIZE = 25;

    private int boardWidth;
    private int boardHeight;

    private Tile snakeHead;
    private ArrayList<Tile> snakeBody;
    private Tile food;
    private Random random;
    private Timer gameLoop;
    private int velocityX;
    private int velocityY;
    private boolean gameOver = false;

    public SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<>();
        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        velocityX = 0;
        velocityY = 0;

        gameLoop = new Timer(75, this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        g.setColor(Color.GREEN);

        for (Tile snakePart : snakeBody) {
            int partX = snakePart.x * TILE_SIZE;
            int partY = snakePart.y * TILE_SIZE;
            g.fillOval(partX + 1, partY + 1, TILE_SIZE - 2, TILE_SIZE - 2);
        }

        g.setColor(Color.WHITE);
        int headX = snakeHead.x * TILE_SIZE;
        int headY = snakeHead.y * TILE_SIZE;
        g.fillOval(headX + 1, headY + 1, TILE_SIZE - 2, TILE_SIZE - 2);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        if (gameOver) {
            g.setColor(Color.RED);
            g.drawString("Game Over: " + snakeBody.size(), TILE_SIZE - 20, TILE_SIZE);
        } else {
            g.setColor(Color.GREEN);
            g.drawString("Score: " + snakeBody.size(), TILE_SIZE - 20, TILE_SIZE);
        }
    }


    public void placeFood() {
        int maxX = boardWidth / TILE_SIZE;
        int maxY = boardHeight / TILE_SIZE;

        ArrayList<Tile> validPositions = new ArrayList<>();

        for (int x = 0; x < maxX; x++) {
            for (int y = 0; y < maxY; y++) {
                Tile position = new Tile(x, y);

                if (!isCollisionWithSnake(position)) {
                    validPositions.add(position);
                }
            }
        }

        if (!validPositions.isEmpty()) {
            int randomIndex = random.nextInt(validPositions.size());
            food = validPositions.get(randomIndex);
        }
    }

    public boolean isCollisionWithSnake(Tile position) {

        if (collision(snakeHead, position)) {
            return true;
        }

        for (Tile snakePart : snakeBody) {
            if (collision(snakePart, position)) {
                return true;
            }
        }

        return false;
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void move() {
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) {
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                Tile prevSnakePart = snakeBody.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        checkWallCollision();

        for (Tile snakePart : snakeBody) {
            if (collision(snakeHead, snakePart)) {
                gameOver = true;
            }
        }
    }

    private void checkWallCollision() {
        if (snakeHead.x < 0) {
            snakeHead.x = boardWidth / TILE_SIZE - 1;
        } else if (snakeHead.x >= boardWidth / TILE_SIZE) {
            snakeHead.x = 0;
        } else if (snakeHead.y < 0) {
            snakeHead.y = boardHeight / TILE_SIZE - 1;
        } else if (snakeHead.y >= boardHeight / TILE_SIZE) {
            snakeHead.y = 0;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}