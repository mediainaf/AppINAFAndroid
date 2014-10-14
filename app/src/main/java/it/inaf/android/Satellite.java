/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class Satellite {

    public String name;

    public class GeoCoordinates {
        public double latitude;
        public double longitude;

        GeoCoordinates(double lat, double lon) {
            latitude = lat;
            longitude = lon;
        }
    }

    Satellite(String cname) {
        name = cname;
    }

    public static final float DEG2RAD = (float) (Math.PI/180.0);
	public static final float RAD2DEG = (float) (180.0/Math.PI);

	private double mu = 398600.8;            // in km3 / s2
	private double radiusearthkm = 6378.135; // km
	private double xke = 0.07436691613;      // 60.0 / sqrt(radiusearthkm*radiusearthkm*radiusearthkm/mu)
	private double vkmpersec = 7.9053705078; // radiusearthkm * xke/60.0
	private double tumin = 13.446839697;     // 1.0 / xke
	private double j2 =   0.001082616;
	private double j3 =  -0.00000253881;
	private double j4 =  -0.00000165597;
	private double j3oj2  =  -0.00234506972; // j3 / j2
	private double sfour  = 1.0122292802;    // 78.0 / radiusearthkm + 1.0
	private double qzms24 = 1.880279159e-09; // ((120.0 - 78.0) / radiusearthkm)**4
	private double x2o3   =  2. / 3.;

	private double mEpoch;
	private double mXincl;
	private double mXnode0;
	private double mE0;
	private double mOmega0;
	private double mXm0;
	private double mXn0;

	private double mTsince;
	private double mMdot;
	private double mArgpdot;
	private double mNodedot;
	private double mAo;
	private double mGmst;
	private double mSinio;
	private double mCosio;
	private double mOmeosq;
	private double mAycof;
	private double mXlcof;

	void readTLE(String line1, String line2) {
		/*
		 *  Parse line1
		 */
		StringTokenizer tokenizer = new StringTokenizer(line1, " ");
		String token = null;
		// skip first 4 tokens
		for(Integer i=0; i<3; i++)
			token = tokenizer.nextToken();

		mEpoch = Double.parseDouble(tokenizer.nextToken());

		/*
		 *  Parse line2
		 */
		tokenizer = new StringTokenizer(line2, " ");
		// skip first 2 tokens
		for(Integer i=0; i<2; i++)
			token = tokenizer.nextToken();

		mXincl = Double.parseDouble(tokenizer.nextToken());
		mXnode0 = Double.parseDouble(tokenizer.nextToken());
		mE0 = Double.parseDouble(tokenizer.nextToken());
		mOmega0 = Double.parseDouble(tokenizer.nextToken());
		mXm0 = Double.parseDouble(tokenizer.nextToken());
		mXn0 = Double.parseDouble(tokenizer.nextToken());

		mE0 *= 1.e-7;
		double eccsq  = mE0 * mE0;
		mOmeosq = 1. - eccsq;
		double rteosq = Math.sqrt(mOmeosq);

		mXincl *= DEG2RAD;
		mCosio  = Math.cos(mXincl);
		mSinio  = Math.sin(mXincl);

		mXnode0 *= DEG2RAD;
		mOmega0 *= DEG2RAD;
		mXm0 *= DEG2RAD;

		mXn0 *= 2 * Math.PI / 1440.;
		double cosio2 = mCosio * mCosio;
		double cosio4 = cosio2 * cosio2;
		double ak     = Math.pow(xke / mXn0, x2o3);
		double d1     = 0.75 * j2 * (3. * cosio2 - 1.) / (rteosq * mOmeosq);
		double del    = d1 / (ak * ak);
		double adel   = ak * (1. - del * (1./3. + del * (1. + 134./81. * del)));
		double ndel   = d1 / (adel * adel);
		mXn0 /= 1.0 + ndel;

		/* ------------------- calculate GMST at epoch ----------------- */
		long ipart = (long) (mEpoch/1000.);
		double fpart = (mEpoch/1000.) - ipart;
		double fday = 1000. * fpart;

		long y = 1999L + ipart;  // 0 AD does not exist!!!
		long A = y / 100L, B = 2 - A + A/4;
		double jd = Math.floor(365.25*y) + Math.floor(30.6001*14) + 1720994.5 + B + fday;  // Jan => 14!
		double UT = Math.IEEEremainder(jd+0.5, 1.); //  universal time
		double TU = (jd - 2451545. - UT) / 36525.;  // centuries
		mGmst = 24110.54841 + TU * (8640184.812866 + TU * (0.093104 - TU * 6.2E-6));
		mGmst = Math.IEEEremainder(mGmst + 86400.*1.00273790934*UT, 86400.);  // GMST in seconds
		mGmst*= 2. * Math.PI / 86400.;  //  GMST in degrees

		// days between Jan 1st and Jan 1st, 2011
		long days = ((long)ipart-11L)*365L + ((long)ipart-9L)/4L;
		ipart = (long) (fday);
		fpart = fday - ipart;
		fday = fpart;
		// add days of the year
		fday+= days+ipart-1L;
		mTsince = 86400.*fday;

		// ------------- calculate auxiliary epoch quantities ----------
		mAo     = Math.pow(xke / mXn0, x2o3);
		double po     = mAo * mOmeosq;
		double con42  = 1.0 - 5.0 * cosio2;
		double pinvsq = 1.0 / po / po;
		double temp1  = 1.5 * j2 * pinvsq * mXn0;
		double temp2  = 0.5 * temp1 * j2 * pinvsq;
		double temp3  = -0.46875 * j4 * pinvsq * pinvsq * mXn0;
		mMdot = mXn0 - 0.5 * temp1 * rteosq * (con42 + 2*cosio2)
                    + 0.0625 * temp2 * rteosq * (13.0 - 78.0 * cosio2 + 137.0 * cosio4);
		mArgpdot = -0.5 * temp1 * con42 + 0.0625 * temp2 * (7.0 - 114.0 * cosio2 + 395.0 * cosio4)
                       + temp3 * (3.0 - 36.0 * cosio2 + 49.0 * cosio4);
		mNodedot = -temp1*mCosio + (0.5 * temp2 * (4.0 - 19.0 * cosio2)
                + 2.0 * temp3 * (3.0 - 7.0 * cosio2)) * mCosio;

		double mXlcof = 0;
		if (Math.abs(mCosio+1.0) > 1.5e-12) // fix for xinc0 close to 180deg
    		mXlcof = -0.25 * j3oj2 * mSinio * (3.0 + 5.0 * mCosio) / (1.0 + mCosio);
		else
			mXlcof = -0.25 * j3oj2 * mSinio * (3.0 + 5.0 * mCosio) / 1.5e-12;
		mAycof = -0.5 * j3oj2 * mSinio;
	}

	GeoCoordinates getLatLong() {
		// ----------- find the number of minutes since EPOCH ----------
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		long year = cal.get(Calendar.YEAR)-2000;
		long days = (year-11L)*365L + (year-9L)/4L + cal.get(Calendar.DAY_OF_YEAR)-1;
		double secs = (((days*24)+cal.get(Calendar.HOUR_OF_DAY))*60+cal.get(Calendar.MINUTE))*60+cal.get(Calendar.SECOND);
		double mins=(secs-mTsince)/60.;

		// ------- update for secular gravity and atmospheric drag -----
		double mm    = Math.IEEEremainder(mXm0 + mMdot * mins, 2*Math.PI);
		double argpm = Math.IEEEremainder(mOmega0 + mArgpdot * mins, 2*Math.PI);
		double nodem = Math.IEEEremainder(mXnode0 + mNodedot * mins, 2*Math.PI);

		// -------------------- add lunar-solar periodics --------------
		double axnl = mE0 * Math.cos(argpm);
		double norm = 1.0 / (mAo * mOmeosq);
		double aynl = mE0 * Math.sin(argpm) + norm * mAycof;

		// --------------------- solve kepler's equation ---------------
		double u    = Math.IEEEremainder(mm + argpm + norm * mXlcof * axnl, 2*Math.PI);
		double eo1  = u;
		double tem5 = 9999.9, coseo1 = 0, sineo1 = 0;
		int ktr = 0;
		for (ktr =0; ktr<10 && Math.abs(tem5)>1.0e-12; ktr++) {
			sineo1 = Math.sin(eo1);
			coseo1 = Math.cos(eo1);
			tem5   = (u - aynl*coseo1 + axnl*sineo1 - eo1) / (1. - coseo1*axnl - sineo1*aynl);
			if (tem5>0.95) tem5=0.95;
			else if (tem5<-0.95) tem5=-0.95;
			eo1 += tem5;
		}

		// --------------------- orientation vectors -------------------
		double el2    = 1.0 - axnl*axnl - aynl*aynl;
		double rl     = 1.0 - axnl*coseo1 - aynl*sineo1;
		double esbe   = (axnl*sineo1 - aynl*coseo1) / (1.0 + Math.sqrt(el2));
		double sinu   = (sineo1 - aynl - axnl * esbe) / rl;
		double cosu   = (coseo1 - axnl + aynl * esbe) / rl;
		double sin2u  = 2.0 * cosu * sinu;
		double cos2u  = 1.0 - 2.0 * sinu * sinu;
		double pl     = mAo*el2;
		double j2pl2  = 0.5 * j2 / pl / pl;
		double su    =  Math.atan2(sinu, cosu) - 0.25 * j2pl2 * sin2u * (7.0*mCosio*mCosio - 1.0);
		double xnode =  nodem + 1.5 * j2pl2 * mCosio * sin2u;
		double xinc  =  mXincl + 1.5 * j2pl2 * mCosio * mSinio * cos2u;
		double sinsu =  Math.sin(su);
		double cossu =  Math.cos(su);
		double snod  =  Math.sin(xnode);
		double cnod  =  Math.cos(xnode);
		double cosi  =  Math.cos(xinc);
		double ux    = -snod * cosi * sinsu + cnod * cossu;
		double uy    =  cnod * cosi * sinsu + snod * cossu;
		double uz    =  Math.sin(xinc) * sinsu;

		// --------- compute the latitude and the longitude ------------
		double outLat = Math.asin(uz/Math.sqrt(ux*ux+uy*uy+uz*uz))/DEG2RAD;
		double outLong = (Math.atan2(uy, ux) - mGmst - 7.29211510e-5*60*mins)/DEG2RAD;
		outLong = Math.IEEEremainder(outLong, 360);
		if (outLong<-180.) outLong+=360.;
		else if (outLong>180.) outLong-=360.;

		return new GeoCoordinates(outLat, outLong);
	}
}
