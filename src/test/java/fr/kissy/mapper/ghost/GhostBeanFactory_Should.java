package fr.kissy.mapper.ghost;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class GhostBeanFactory_Should {

    @InjectMocks
    private GhostBeanFactory beanFactory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

}