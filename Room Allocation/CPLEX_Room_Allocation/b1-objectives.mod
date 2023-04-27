/*********************************************
 * OPL 12.10.0.0 Model
 * Author: YISHAIFELDMAN
 * Creation Date: 30 Sep 2020 at 18:53:18
 *********************************************/

minimize 
       20000 * (sum(a in areas, f in floors) cost_penalty[a][f])
       + 
       1 * total_cost
       ;