package zyz.wss;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class WSSComponentServiceTests {
    /* @Autowired
    private UserRepository userRepository;

    @Autowired
    private WSSFolderRepository folderRepository;

    @Autowired
    private WSSUserFileRepository userFileRepository;

    @Autowired
    private WSSComponentService cs;

    @Test
    public void test() {
        User u = userRepository.findById(1).get();
        WSSFolder f = folderRepository.findById("2").get();

        Assert.isTrue(userFileRepository.findByParentAndNameAndOwner(f, "闪翼拳皇1.91.swf", u).size() > 0, "error");
    } */
}