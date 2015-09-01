
-- name: find-places
SELECT places.id, places.title, lat, lng, place_categories.title AS category
FROM places INNER JOIN place_categories
	ON places.place_category_id = place_categories.id;


-- name: find-areas
SELECT `boundary-string`, areas.title AS lga, regions.colour AS colour, regions.title AS region, areas.id
FROM areas INNER JOIN regions
	ON areas.region_id = regions.id;


-- name: find-facts-by-place-id
SELECT places.id, places.title AS place, facts.title, place_facts.detail_text, place_facts.detail_value, fact_categories.title AS category
FROM place_facts
	INNER JOIN places ON place_facts.place_id=places.id
    INNER JOIN facts ON place_facts.fact_id=facts.id
    INNER JOIN fact_categories ON facts.fact_category_id=fact_categories.id
WHERE place_id = ?;


-- name: find-area-by-id
SELECT id, title
FROM areas
WHERE id = ?;


-- name: find-facts-by-area-id
SELECT areas.id, areas.title AS area, areas.region_id, facts.id AS fact_id, facts.title, area_facts.detail_text, area_facts.detail_value, fact_categories.title AS category
FROM area_facts
	INNER JOIN areas ON area_facts.area_id=areas.id
    INNER JOIN facts ON area_facts.fact_id=facts.id
    INNER JOIN fact_categories ON facts.fact_category_id=fact_categories.id
WHERE area_id = ?;


-- name: find-fact-averages-by-region-and-category
SELECT facts.title, facts.id AS fact_id, AVG(area_facts.detail_value) AS detail_value
FROM area_facts
       INNER JOIN areas ON area_facts.area_id=areas.id
    INNER JOIN facts ON area_facts.fact_id=facts.id
    INNER JOIN fact_categories ON facts.fact_category_id=fact_categories.id
    INNER JOIN regions ON areas.region_id=regions.id
WHERE regions.id = ?
AND fact_categories.id = ?
GROUP BY title;
