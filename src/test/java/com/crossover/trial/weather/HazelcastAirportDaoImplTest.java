package com.crossover.trial.weather;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by ekonovalov on 24.03.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/hazelcastContext.xml")
public class HazelcastAirportDaoImplTest extends AirportDaoImplTest {
}
