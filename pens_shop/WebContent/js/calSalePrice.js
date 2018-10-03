//For CalSalePrice
function calDrugSalePrice(package,packagePrice){
	var rawPrice=packagePrice/package;
	var salePrice=0;
	//alert(rawPrice);
	if(rawPrice>=0.01 && rawPrice<=0.2){
		salePrice=0.5;
	}
	else if(rawPrice>=0.21 && rawPrice<=0.5){
		salePrice=1;
	}
	else if(rawPrice>=0.51 && rawPrice<=1){
		salePrice=1.5;
	}
	else if(rawPrice>=1.01 && rawPrice<=5){
		salePrice=1.5+(1.25*(rawPrice-1));
	}
	else if(rawPrice>=5.01 && rawPrice<=10){
		salePrice=6.5+(1.2*(rawPrice-5));
	}
	else if(rawPrice>=10.01 && rawPrice<=50){
		salePrice=12.5+(1.18*(rawPrice-10));
	}
	else if(rawPrice>=50.01 && rawPrice<=100){
		salePrice=60+(1.16*(rawPrice-50));
	}
	else if(rawPrice>=100.01 && rawPrice<=500){
		salePrice=118+(1.14*(rawPrice-100));
	}
	else if(rawPrice>=500.01 && rawPrice<=1000){
		salePrice=574+(1.12*(rawPrice-500));
	}
	else if(rawPrice>=1000.01 && rawPrice<=5000){
		salePrice=1134+(1.1*(rawPrice-1000));
	}
	else if(rawPrice>=5000.01 && rawPrice<=10000){
		salePrice=5534+(1.08*(rawPrice-5000));
	}
	else if(rawPrice>10000){
		salePrice=10934+(1.06*(rawPrice-10000));
	}
	//alert(salePrice);
	var sl = salePrice.toFixed(2).toString().split('.');
	//alert(sl);
	
	if(sl.length>1){
		if(salePrice<10){
			if(sl[1]>0 && sl[1]<12.5){
				sl[1]=0;
			}
			else if(sl[1]>=12.5 && sl[1]<37.5){
				sl[1]=25;
			}
			else if(sl[1]>=37.5 && sl[1]<62.5){
				sl[1]=50;
			}
			else if(sl[1]>=62.5 && sl[1]<87.5){
				sl[1]=75;
			}
			else if(sl[1]>=87.5){
				sl[0]=eval(sl[0])+1;
				sl[1]=0;
			}
			salePrice = sl[0]+'.'+sl[1];
		}
		else if(salePrice>10 && salePrice<=100){
			if(sl[1]>0 && sl[1]<25){
				sl[1]=0;
			}
			else if(sl[1]>=25 && sl[1]<75){
				sl[1]=50;
			}
			else if(sl[1]>=75){
				sl[0]=eval(sl[0])+1;
				sl[1]=0;
			}
			salePrice = sl[0]+'.'+sl[1];
		}
		else if(salePrice>100){
			if(sl[1]>50){
				sl[0]=eval(sl[0])+1;
			}
			salePrice = sl[0]+'.00';
		}
	}
	//alert(salePrice);
	return salePrice;
}