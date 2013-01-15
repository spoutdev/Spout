import bpy;
from bpy_extras.io_utils import ExportHelper;
from bpy.props import IntProperty;
from mathutils import Matrix;

bl_info = {
    "name":         "Spout Animation Model",
    "author":       "Karang",
    "blender":      (2,6,2),
    "version":      (0,0,1),
    "location":     "File > Import-Export",
    "description":  "Export bones animation for spout engine.",
    "category":     "Import-Export"
}

class ExportSAM(bpy.types.Operator, ExportHelper):
    bl_idname       = "export_anim.sam";
    bl_label        = "Spout Animation Model";
    bl_options      = {'PRESET'};
    
    filename_ext    = ".sam";

    startFrame_prop = IntProperty(name="Start frame",
                                  description="Starting frame for the animation",
                                  default=1);
    endFrame_prop = IntProperty(name="End frame",
                                description="Ending frame for the animation",
                                default=250);

    def mul(self, mat1, mat2):
        return [[mat1[0][0]*mat2[0][0]+mat1[0][1]*mat2[1][0]+mat1[0][2]*mat2[2][0]+mat1[0][3]*mat2[3][0],
                mat1[0][0]*mat2[0][1]+mat1[0][1]*mat2[1][1]+mat1[0][2]*mat2[2][1]+mat1[0][3]*mat2[3][1],
                mat1[0][0]*mat2[0][2]+mat1[0][1]*mat2[1][2]+mat1[0][2]*mat2[2][2]+mat1[0][3]*mat2[3][2],
                mat1[0][0]*mat2[0][3]+mat1[0][1]*mat2[1][3]+mat1[0][2]*mat2[2][3]+mat1[0][3]*mat2[3][3]],
                [mat1[1][0]*mat2[0][0]+mat1[1][1]*mat2[1][0]+mat1[1][2]*mat2[2][0]+mat1[1][3]*mat2[3][0],
                mat1[1][0]*mat2[0][1]+mat1[1][1]*mat2[1][1]+mat1[1][2]*mat2[2][1]+mat1[1][3]*mat2[3][1],
                mat1[1][0]*mat2[0][2]+mat1[1][1]*mat2[1][2]+mat1[1][2]*mat2[2][2]+mat1[1][3]*mat2[3][2],
                mat1[1][0]*mat2[0][3]+mat1[1][1]*mat2[1][3]+mat1[1][2]*mat2[2][3]+mat1[1][3]*mat2[3][3]],
                
                [mat1[2][0]*mat2[0][0]+mat1[2][1]*mat2[1][0]+mat1[2][2]*mat2[2][0]+mat1[2][3]*mat2[3][0],
                 mat1[2][0]*mat2[0][1]+mat1[2][1]*mat2[1][1]+mat1[2][2]*mat2[2][1]+mat1[2][3]*mat2[3][1],
                 mat1[2][0]*mat2[0][2]+mat1[2][1]*mat2[1][2]+mat1[2][2]*mat2[2][2]+mat1[2][3]*mat2[3][2],
                 mat1[2][0]*mat2[0][3]+mat1[2][1]*mat2[1][3]+mat1[2][2]*mat2[2][3]+mat1[2][3]*mat2[3][3]],
                
                [mat1[3][0]*mat2[0][0]+mat1[3][1]*mat2[1][0]+mat1[3][2]*mat2[2][0]+mat1[3][3]*mat2[3][0],
                 mat1[3][0]*mat2[0][1]+mat1[3][1]*mat2[1][1]+mat1[3][2]*mat2[2][1]+mat1[3][3]*mat2[3][1],
                 mat1[3][0]*mat2[0][2]+mat1[3][1]*mat2[1][2]+mat1[3][2]*mat2[2][2]+mat1[3][3]*mat2[3][2],
                 mat1[3][0]*mat2[0][3]+mat1[3][1]*mat2[1][3]+mat1[3][2]*mat2[2][3]+mat1[3][3]*mat2[3][3]]];

        
    def getMatrix(self, bone):
        if (bone.parent is None):
            return bone.matrix_channel;
        else:
            return self.mul(self.getMatrix(bone.parent), bone.matrix_channel);

    def writeAnimation(self, f, scene, startFrame, endFrame, bone):
        indent = "        ";
        for frame in range(startFrame, endFrame+1):
            scene.frame_set(frame);
            rot = [[1,0,0,0],[0,1,0,0],[0,0,1,0],[0,0,0,1]]
            m = self.mul(rot, self.getMatrix(bone));
            f.write(indent + str(frame) + ": ");
            f.write("%.6f, %.6f, %.6f, %.6f, " % (m[0][0], m[1][0], m[2][0], m[3][0]));
            f.write("%.6f, %.6f, %.6f, %.6f, " % (m[0][1], m[1][1], m[2][1], m[3][1]));
            f.write("%.6f, %.6f, %.6f, %.6f, " % (m[0][2], m[1][2], m[2][2], m[3][2]));
            f.write("%.6f, %.6f, %.6f, %.6f" % (m[0][3], m[1][3], m[2][3], m[3][3]));
            f.write("\n");

    def saveSAM(self, context, path, startFrame, endFrame):
        f = open(path, "w", encoding="utf8", newline="\n");

        scene = context.scene;
        obj = context.object;
        armature = obj.parent;

        f.write("Skeleton: skeleton://please edit path to skeleton.ske\n");
        f.write("frames: %d\n" % (endFrame - startFrame + 1));
        f.write("delay: %.6f\n" % (1.0 / (scene.render.fps / scene.render.fps_base)));
        f.write("bones_data:\n");
        
        for bone in armature.pose.bones:
            f.write("    " + bone.name + ":\n");
            self.writeAnimation(f, scene, startFrame, endFrame, bone);
        
        f.close();
        return {'FINISHED'};
    
    def execute(self, context):
        path = self.as_keywords()["filepath"];
        start = self.as_keywords()["startFrame_prop"];
        end = self.as_keywords()["endFrame_prop"];
        return self.saveSAM(context, path, start, end);

def menu_func(self, context):
    self.layout.operator(ExportSAM.bl_idname, text="Spout Animation Model (.sam)");

def register():
    bpy.utils.register_module(__name__);
    bpy.types.INFO_MT_file_export.append(menu_func);
    
def unregister():
    bpy.utils.unregister_module(__name__);
    bpy.types.INFO_MT_file_export.remove(menu_func);

if __name__ == "__main__":
    register()
