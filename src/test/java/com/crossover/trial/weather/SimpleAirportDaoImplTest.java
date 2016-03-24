package com.crossover.trial.weather;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Airport DAO tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/simpleContext.xml")
public class SimpleAirportDaoImplTest extends AirportDaoImplTest {
}
