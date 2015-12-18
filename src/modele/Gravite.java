package modele;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;


public class Gravite extends ObservableModele implements ActionListener {

  /** Constate gravitationnel */
  private double g = 9.81;
  /** pas de la simulation */
  private double dt = 0.01;
  /** vecteur position */
  private double[] position = new double[] {0.0, 0.0};
  /** vecteur vitesse */
  private double[] vitesse = new double[] {90.0, 100.0};
  /** coefficients pour les frottements de l'air pour l'oiseau */
  private double k = 0.001;
  /** coefficients pour les frottements de l'air pour les obstacles */
  private double kObstacles = 0.001;
  /** poids de l'oiseau */
  private double poidsOiseau = 50.0;
  /** poids des obstacles */
  private double poidsObstacle = 150.0;
  private Timer t;

  /**
   * Constructeur de base
   */
  public Gravite() {
    super();
    genObstacles();
  }

  /**
   * @see Modele
   */
  @Override
  public void go() {
    t = new Timer(1, this);
    t.start();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (position[1] >= 0) {
      for (Obstacle obstacle : obstacles) {
        acceleration(obstacle);
        deplacement(obstacle);
      }
      acceleration();
      deplacement();
      courbe.add(new Point((int) position[0], (int) position[1]));
      setChanged();
      notifyObservers();
      checkIfDone();
      done = false;
    } else {
      done = true;
      t.stop();
    }
  }

  private void setVitesse(double x, double z) {
    vitesse[0] = x;
    vitesse[1] = z;
  }

  /**
   * @see Modele
   */
  @Override
  public void reset() {
    courbe = new ArrayList<Point>();
    obstacles = new ArrayList<>();
    position = new double[] {0.0, 0.0};
    vitesse = new double[] {90.0, 100.0};
    genObstacles();
    collision = false;
    done = false;
    k = 0.001;
  }

  /**
   * Calcul la vitesse de proche en proche
   */
  private void acceleration() {
    double dx = vitesse[0];
    double dz = vitesse[1];
    dz = dz + dt * (-g) - k * vitesse[1];
    dx = dx - k * vitesse[0];
    vitesse[0] = dx;
    vitesse[1] = dz;
  }


  /**
   * Calcul la position de proche en proche
   */
  private void deplacement() {
    double x = position[0];
    double z = position[1];
    double dx = vitesse[0];
    double dz = vitesse[1];
    x = x + dx * dt;
    z = z + dz * dt;
    position[0] = x;
    position[1] = z;
  }

  /**
   * Calcul la vitesse de proche en proche pour l'obstacle passer en parametre
   * 
   * @param obstacle l'obstacle qui doit se deplacer
   */
  private void acceleration(Obstacle obstacle) {
    double dx = obstacle.getVitesse()[0];
    double dz = obstacle.getVitesse()[1];
    if (obstacle.getMoveZ()) {
      dz = dz + dt * (-g);
    }
    if (obstacle.isTouched()) {
      // subit les frottements de l'air
      dz -= kObstacles * obstacle.getVitesse()[1];
      dx -= kObstacles * obstacle.getVitesse()[0];
    }
    obstacle.setVitesse(dx, dz);
  }

  /**
   * Calcul la position de proche en proche pour l'obstacle passer en parametre
   * 
   * @param obstacle l'obstacle qui doit se deplacer
   */
  private void deplacement(Obstacle obstacle) {
    double x = obstacle.getPosition()[0];
    double z = obstacle.getPosition()[1];
    double dx = obstacle.getVitesse()[0];
    double dz = obstacle.getVitesse()[1];
    if (obstacle.getMoveX()) {
      x = x + dx * dt;
    }
    if (obstacle.getMoveZ()) {
      z = z + dz * dt;
    }
    obstacle.setPosition(x, z);
  }

  /**
   * inverse l'axe Y
   * 
   * @param y
   * @return
   */
  public int inverse(int y) {
    return -y + 470;
  }

  public void setK(double d) {
    this.k = d;
  }

  public double getK() {
    return k;
  }

  /**
   * verifie si la simulation doit se terminer c�d si l'oiseau est sur le sol et ne rebondit plus
   */
  private void checkIfDone() {

  }

  /**
   * @see Modele
   */
  @Override
  public void collision(Obstacle o) {
    double[] vg =
        new double[] {
            (poidsOiseau * vitesse[0] + poidsObstacle * o.getVitesse()[0])
                / (poidsObstacle + poidsOiseau),
            (poidsOiseau * vitesse[1] + poidsObstacle * o.getVitesse()[1])
                / (poidsObstacle + poidsOiseau)};
    setVitesse(2 * vg[0] - vitesse[0], 2 * vg[1] - vitesse[1]);
    o.setVitesse(2 * vg[0] - o.getVitesse()[0], 2 * vg[1] - o.getVitesse()[1]);
  }

  /**
   * @see Modele
   */
  @Override
  public void collision(Obstacle o1, Obstacle o2) {
    double[] vg =
        new double[] {
            (poidsObstacle * o1.getVitesse()[0] + poidsObstacle * o2.getVitesse()[0])
                / (poidsObstacle + poidsObstacle),
            (poidsObstacle * o1.getVitesse()[1] + poidsObstacle * o2.getVitesse()[1])
                / (poidsObstacle + poidsObstacle)};
    o1.setVitesse(2 * vg[0] - o1.getVitesse()[0], 2 * vg[1] - o1.getVitesse()[1]);
    o2.setVitesse(2 * vg[0] - o2.getVitesse()[0], 2 * vg[1] - o2.getVitesse()[1]);
  }

}
