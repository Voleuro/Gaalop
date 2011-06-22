package de.gaalop.gappZwei.cfgimport;

import de.gaalop.tba.Algebra;
import de.gaalop.tba.IMultTable;
import de.gaalop.tba.MultTableImpl;
import de.gaalop.tba.MultTableLoader;
import de.gaalop.tba.Multivector;

public class UseAlgebra {

	private Algebra algebra;
	private IMultTable tableInner;
	private IMultTable tableOuter;
	private IMultTable tableGeo;
	
	public void load5dAlgebra() {
		algebra = new Algebra("bladesGA5d.csv");
		
		tableInner = new MultTableImpl();
		tableOuter = new MultTableImpl();
		tableGeo = new MultTableImpl();
		MultTableLoader loader = new MultTableLoader();
		
		loader.load(tableInner, tableOuter, tableGeo, algebra,"productsGA5d.csv","replacesGA5d.csv");
	}
	
	public Multivector inner(Integer factor1, Integer factor2) {
		return tableInner.getProduct(factor1, factor2);
	}
	
	public Multivector outer(Integer factor1, Integer factor2) {
		return tableOuter.getProduct(factor1, factor2);
	}
	
	public Multivector geo(Integer factor1, Integer factor2) {
		return tableGeo.getProduct(factor1, factor2);
	}

	public int getBladeCount() {
		return algebra.getBlades().size();
	}
	
	public int getGrade(int blade) {
		return algebra.getBlade(blade).getBases().size();
	}
	
}
