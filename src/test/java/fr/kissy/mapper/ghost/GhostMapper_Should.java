package fr.kissy.mapper.ghost;

import com.google.common.collect.Lists;
import fr.kissy.mapper.ghost.test.Simple_Valid_Bean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GhostMapper_Should {

    private Simple_Valid_Bean expectedSimpleValidBean = new Simple_Valid_Bean();
    @Mock
    private WebDriver webDriver;
    @Mock
    private GhostBeanMapper ghostBeanMapper;
    @InjectMocks
    private GhostMapper ghostMapper;

    @Before
    public void mock_ghost_bean_mapper() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(ghostBeanMapper.readBean(webDriver, webDriver, Simple_Valid_Bean.class, Lists.<String>newArrayList()))
                .thenReturn(expectedSimpleValidBean);
    }

    @Test
    public void use_bean_mapper_to_read_value() throws Exception {
        Simple_Valid_Bean simpleValidBean = ghostMapper.readValue(webDriver, Simple_Valid_Bean.class);
        assertEquals(expectedSimpleValidBean, simpleValidBean);
        verify(ghostBeanMapper, times(1)).readBean(webDriver, webDriver, Simple_Valid_Bean.class, Lists.<String>newArrayList());
    }
}