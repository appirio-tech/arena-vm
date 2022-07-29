-- This script creates a table holding the definition of room assignment
-- algorithm for each contest round. This data contain number of coders 
-- per room, type of room assignment algorithm (iron man, classic, etc.),
-- and other parameters.
-- 
create table round_room_assignment (
     round_id DECIMAL(10,0),
     coders_per_room DECIMAL(10,0),
     algorithm DECIMAL(1,0),
     by_division DECIMAL(1,0),
     by_region DECIMAL(1,0),
     final DECIMAL(1,0),
     p DECIMAL(10,2)
);

