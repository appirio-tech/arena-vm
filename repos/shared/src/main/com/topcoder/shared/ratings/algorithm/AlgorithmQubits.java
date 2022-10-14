/*
 * AlgorithmQubits.java
 *
 * Created on January 4, 2007, 8:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.shared.ratings.algorithm;

import com.topcoder.shared.ratings.model.QubitsRatingData;
import com.topcoder.shared.ratings.model.RatingData;
import com.topcoder.shared.util.logging.Logger;

/**
 * The Qubits rating algorithm
 *
 * @author rfairfax
 */
public class AlgorithmQubits implements Algorithm {
    
    private QubitsRatingData[] data;
    private Logger log = Logger.getLogger(AlgorithmQubits.class);
    
    /** Creates a new instance of AlgorithmQubits */
    public AlgorithmQubits() {
    }

    /**
     * Gives the algorithm list of rating data to use for this run
     * @param data Array of data to use for this rating run
     * @see com.topcoder.shared.ratings.algorithm.Algorithm
     */
    public void setRatingData(RatingData[] data) {
        //convert each record into QubitsRatingData
        this.data = new QubitsRatingData[data.length];
        for(int i = 0; i < data.length; i++) {
            this.data[i] = new QubitsRatingData(data[i]);
        }
    }

    /**
     * Returns modified rating data after run
     * @return array of modified rating data
     * @see com.topcoder.shared.ratings.algorithm.Algorithm
     */
    public RatingData[] getRatingData() {
        //no need to clone anything
        return data;
    }

    /**
     * Runs the Qubits algorithm against rating data.
     * 
     * This formula is used by algorithm and marathon events.
     */
    public void runRatings() {
        log.info("Starting rating run for " + data.length + " entries");
        
        //setup default values for new players
        for(int i = 0; i < data.length; i++) {
            if(data[i].getNumRatings() == 0) {
                data[i].setVolatility(515);
                data[i].setRating(1200);
            }
        }
        
        /* COMPUTE AVERAGE RATING */
        double rave = 0.0;
        for (int i = 0; i < data.length; i++) {
            rave += (double)data[i].getRating();
        }
        rave /= data.length;
        
        log.debug("Average Rating is: " + rave);
        
        /* COMPUTE COMPETITION FACTOR */
        double rtemp = 0, vtemp = 0;
        for (int i = 0; i < data.length; i++) {
            vtemp += sqr((double)data[i].getVolatility());
            rtemp += sqr((double)data[i].getRating() - rave);
        }
        
        double matchStdDevEquals = Math.sqrt(vtemp / data.length + rtemp / (data.length - 1));
        log.debug("Competition Factor is: " + matchStdDevEquals);
        
        /* COMPUTE EXPECTED RANKS */
        for (int i = 0; i < data.length; i++) {
            double est = 0.5;
            double myskill = ((double)data[i].getRating() - INITIAL_SCORE) / ONE_STD_DEV_EQUALS;
            double mystddev = ((double)data[i].getVolatility()) / ONE_STD_DEV_EQUALS;
            for (int j = 0; j < data.length; j++) {
                est += winprobability((double)data[j].getRating(), (double)data[i].getRating(),
                        (double)data[j].getVolatility(), (double)data[i].getVolatility());
            }
            data[i].setExpectedRank(est);
            data[i].setExpectedPerformance(-normsinv((est - .5) / data.length));
        }
        
        /* COMPUTE ACTUAL RANKS */
        for (int i = 0; i < data.length;) {
            double max = Double.NEGATIVE_INFINITY;
            int count = 0;

            for (int j = 0; j < data.length; j++) {
                if (data[j].getScore() >= max && data[j].getActualRank() == 0) {
                    if (data[j].getScore() == max)
                        count++;
                    else
                        count = 1;
                    max = data[j].getScore();
                }
            }
            for (int j = 0; j < data.length; j++) {
                if (data[j].getScore() == max) {
                    data[j].setActualRank(i + 0.5 + count / 2.0);
                    data[j].setActualPerformance(-normsinv((i + count / 2.0) / data.length));
                }
            }
            i += count;
        }
        
        /* UPDATE RATINGS */
        for (int i = 0; i < data.length; i++) {
            double diff = data[i].getActualPerformance() - data[i].getExpectedPerformance();

            double oldrating = (int)data[i].getRating();
            double performedAs = oldrating + diff * matchStdDevEquals;
            double weight = (INITIAL_WEIGHT - FINAL_WEIGHT) / (data[i].getNumRatings() + 1) + FINAL_WEIGHT;

            //get weight - reduce weight for highly rated people
            weight = 1 / (1 - weight) - 1;
            if (oldrating >= 2000 && oldrating < 2500) weight = weight * 4.5 / 5.0;
            if (oldrating >= 2500) weight = weight * 4.0 / 5.0;

            double newrating = (oldrating + weight * performedAs) / (1 + weight);

            //get and inforce a cap
            double cap = 150 + 1500 / (2 + (data[i].getNumRatings()));
            if (oldrating - newrating > cap) newrating = oldrating - cap;
            if (newrating - oldrating > cap) newrating = oldrating + cap;
            if (newrating < 1) newrating = 1;

            data[i].setRating((int)Math.round(newrating));

            if (data[i].getNumRatings() != 0) {
                double oldVolatility = (double)data[i].getVolatility();
                data[i].setVolatility((int)Math.round(Math.sqrt((oldVolatility*oldVolatility) / (1+weight) + ((newrating-oldrating)*(newrating-oldrating))/ weight)));
            } else {
                data[i].setVolatility((int)Math.round(FIRST_VOLATILITY));
            }
        }
        
        //increment number of ratings for everyone
        for(int i = 0; i < data.length; i++) {
            data[i].setNumRatings(data[i].getNumRatings() + 1);
        }
        
        //debug output
        for(int i = 0; i < data.length; i++) {
            log.debug("CDR: " + data[i].getCoderID() + ":" + data[i].getRating() + "," + data[i].getVolatility());
        }
    }
    
    //constants
    private static final double INITIAL_SCORE = 1200.0;
    private static final double ONE_STD_DEV_EQUALS = 1200.0; /* rating points */
    private static final double INITIAL_WEIGHT = 0.60;
    private static final double FINAL_WEIGHT = 0.18;
    private static final double FIRST_VOLATILITY = 385;
    
    //Math utility funcs
    private double sqr(double j) {
        return j * j;
    }
    
    private double winprobability(double r1, double r2, double v1, double v2) {
        return (erf((r1-r2)/Math.sqrt(2.0*(v1*v1+v2*v2)))+1.0)*.5;
    }
    
    private static double erf(double z) {
        double t = 1.0 / (1.0 + 0.5 * Math.abs(z));

        // use Horner's method
        double ans = 1 - t * Math.exp( -z*z   -   1.26551223 +
                                            t * ( 1.00002368 +
                                            t * ( 0.37409196 + 
                                            t * ( 0.09678418 + 
                                            t * (-0.18628806 + 
                                            t * ( 0.27886807 + 
                                            t * (-1.13520398 + 
                                            t * ( 1.48851587 + 
                                            t * (-0.82215223 + 
                                            t * ( 0.17087277))))))))));
        if (z >= 0) return  ans;
        else        return -ans;
    }
    
    private static double normsinv(double p) {
    /* ********************************************
     * Original algorythm and Perl implementation can
     * be found at:
     * http://www.math.uio.no/~jacklam/notes/invnorm/index.html
     * Author:
     *  Peter J. Acklam
     *  jacklam@math.uio.no
     * ****************************************** */
        
        // Define break-points.
        // variable for result
        if(p <= 0) return Double.NEGATIVE_INFINITY;
        else if(p >= 1) return Double.POSITIVE_INFINITY;
        
        double z = 0;

        // Rational approximation for lower region:
        if( p < P_LOW )
        {
          double q  = Math.sqrt(-2*Math.log(p));
          z = (((((NORMINV_C[0]*q+NORMINV_C[1])*q+NORMINV_C[2])*q+NORMINV_C[3])*q+NORMINV_C[4])*q+NORMINV_C[5]) / ((((NORMINV_D[0]*q+NORMINV_D[1])*q+NORMINV_D[2])*q+NORMINV_D[3])*q+1);
        }
        // Rational approximation for upper region:
        else if ( P_HIGH < p )
        {
          double q  = Math.sqrt(-2*Math.log(1-p));
          z = -(((((NORMINV_C[0]*q+NORMINV_C[1])*q+NORMINV_C[2])*q+NORMINV_C[3])*q+NORMINV_C[4])*q+NORMINV_C[5]) / ((((NORMINV_D[0]*q+NORMINV_D[1])*q+NORMINV_D[2])*q+NORMINV_D[3])*q+1);
        }
        // Rational approximation for central region:
        else
        {
          double q = p - 0.5D;
          double r = q * q;
          z = (((((NORMINV_A[0]*r+NORMINV_A[1])*r+NORMINV_A[2])*r+NORMINV_A[3])*r+NORMINV_A[4])*r+NORMINV_A[5])*q / (((((NORMINV_B[0]*r+NORMINV_B[1])*r+NORMINV_B[2])*r+NORMINV_B[3])*r+NORMINV_B[4])*r+1);
        }
        
        z = refine(z, p);
        return z;
    }
    
    private static double erfc(double z) {
        return 1.0 - erf(z);
    }

    private static double refine(double x, double d)
    {
        if( d > 0 && d < 1)
        {
          double e = 0.5D * erfc(-x/Math.sqrt(2.0D)) - d;
          double u = e * Math.sqrt(2.0D*Math.PI) * Math.exp((x*x)/2.0D);
          x = x - u/(1.0D + x*u/2.0D);
        }
        return x;
    }
    

    private static final double P_LOW  = 0.02425D;
    private static final double P_HIGH = 1.0D - P_LOW;

    // Coefficients in rational approximations.
    private static final double NORMINV_A[] =
    { -3.969683028665376e+01,  2.209460984245205e+02,
    -2.759285104469687e+02,  1.383577518672690e+02,
    -3.066479806614716e+01,  2.506628277459239e+00 };

    private static final double NORMINV_B[] =
    { -5.447609879822406e+01,  1.615858368580409e+02,
    -1.556989798598866e+02,  6.680131188771972e+01,
    -1.328068155288572e+01 };

    private static final double NORMINV_C[] =
    { -7.784894002430293e-03, -3.223964580411365e-01,
    -2.400758277161838e+00, -2.549732539343734e+00,
    4.374664141464968e+00,  2.938163982698783e+00 };

    private static final double NORMINV_D[] =
    { 7.784695709041462e-03,  3.224671290700398e-01,
    2.445134137142996e+00,  3.754408661907416e+00 };
}
